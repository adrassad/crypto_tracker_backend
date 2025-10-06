package com.adrassad.cryprice.scheduler.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Название задачи для отображения в админке
    private String cron; // CRON-выражение
    private boolean enabled = true;
    @Column(length = 2048)
    private String params; // JSON с параметрами задачи (если нужны)
    private LocalDateTime lastRun;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCron() {
        return cron;
    }
    public void setCron(String cron) {
        this.cron = cron;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public LocalDateTime getLastRun() {
        return lastRun;
    }
    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }   
    // Геттеры и сеттеры
}