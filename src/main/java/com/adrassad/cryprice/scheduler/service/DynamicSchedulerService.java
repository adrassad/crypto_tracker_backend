package com.adrassad.cryprice.scheduler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.adrassad.cryprice.scheduler.entity.ScheduledTask;
import com.adrassad.cryprice.scheduler.repository.ScheduledTaskRepository;

/**
 * Динамически планирует задачи из БД. При изменениях админки вызываем reloadSchedules().
 * В одно-процессной среде ConcurrentTaskScheduler достаточно. Для кластера рекомендуется ShedLock/Quartz.
 */
@Service
public class DynamicSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(DynamicSchedulerService.class);

    private final ScheduledTaskRepository taskRepository;
    private final TaskExecutorService executorService;

    private final Map<Long, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
    private final TaskScheduler scheduler = new ConcurrentTaskScheduler();

    public DynamicSchedulerService(ScheduledTaskRepository taskRepository, TaskExecutorService executorService) {
        this.taskRepository = taskRepository;
        this.executorService = executorService;
    }

    @PostConstruct
    public void init() {
        reloadSchedules();
    }

    public synchronized void reloadSchedules() {
        log.info("Reloading schedules...");
        // cancel old
        futures.values().forEach(f -> f.cancel(false));
        futures.clear();

        List<ScheduledTask> tasks = taskRepository.findByEnabledTrue();
        for (ScheduledTask t : tasks) {
            try {
                CronTrigger trigger = new CronTrigger(t.getCron());
                ScheduledFuture<?> future = scheduler.schedule(() -> runTaskAsync(t), trigger);
                futures.put(t.getId(), future);
                log.info("Scheduled task {} with cron {}", t.getName(), t.getCron());
            } catch (Exception ex) {
                log.error("Invalid cron for task {}: {}", t.getName(), ex.getMessage());
            }
        }
    }

    private void runTaskAsync(ScheduledTask t) {
        log.info("Running task {} at {}", t.getName(), LocalDateTime.now());
        // execute and update lastRun
        try {
            Mono<Void> mono = executorService.execute(t);
            mono.block(); // blocking because scheduler expects Runnable; in reactive project change strategies
            t.setLastRun(LocalDateTime.now());
            taskRepository.save(t);
        } catch (Exception ex) {
            log.error("Error executing task {}: {}", t.getName(), ex.getMessage(), ex);
        }
    }
}