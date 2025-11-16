package com.adrassad.cryprice.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adrassad.cryprice.scheduler.entity.ScheduledTask;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    List<ScheduledTask> findByEnabledTrue();
    Optional<ScheduledTask> findByName(String name);
}