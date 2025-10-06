package com.adrassad.cryprice.scheduler.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrassad.cryprice.scheduler.entity.ScheduledTask;
import com.adrassad.cryprice.scheduler.repository.ScheduledTaskRepository;
import com.adrassad.cryprice.scheduler.service.DynamicSchedulerService;

@RestController
@RequestMapping("/api/admin/scheduler")
public class ScheduledTaskController {
    @Autowired
    private ScheduledTaskRepository repo;
    @Autowired
    private DynamicSchedulerService schedulerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ScheduledTask create(@RequestBody ScheduledTask task) {
        ScheduledTask saved = repo.save(task);
        schedulerService.reloadSchedules();
        return saved;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ScheduledTask update(@PathVariable Long id, @RequestBody ScheduledTask task) {
        task.setId(id);
        ScheduledTask saved = repo.save(task);
        schedulerService.reloadSchedules();
        return saved;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
        schedulerService.reloadSchedules();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ScheduledTask> getAll() {
        return repo.findAll();
    }

}