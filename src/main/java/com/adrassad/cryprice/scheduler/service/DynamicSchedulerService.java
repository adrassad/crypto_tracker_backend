package com.adrassad.cryprice.scheduler.service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.adrassad.cryprice.scheduler.entity.ScheduledTask;
import com.adrassad.cryprice.scheduler.repository.ScheduledTaskRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DynamicSchedulerService {
    @Autowired
    private ScheduledTaskRepository repo;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TaskScheduler scheduler = new ConcurrentTaskScheduler();

    @PostConstruct
    public void init() {
        reloadSchedules();
    }

    // Вызывай после каждого изменения расписания (через админку)
    public void reloadSchedules() {
        scheduledTasks.values().forEach(f -> f.cancel(false));
        scheduledTasks.clear();

        List<ScheduledTask> tasks = repo.findByEnabledTrue();
        for (ScheduledTask task : tasks) {
            ScheduledFuture<?> future = scheduler.schedule(
                () -> runTask(task),
                new CronTrigger(task.getCron())
            );
            scheduledTasks.put(task.getId(), future);
        }
    }

    private void runTask(ScheduledTask task) {
        // Здесь логика твоей задачи.
        // Можно распарсить task.getParams() если нужно.
        System.out.println("Выполняется задача: " + task.getName());
        // TODO: Реализуй действия по task.getName()
    }
}