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

    // Уникальное имя задачи, напр. "fetch_crypto_prices"
    @Column(nullable = false, unique = true)
    private String name;

    // CRON expression
    @Column(nullable = false)
    private String cron;

    // true если задача включена
    @Column(nullable = false)
    private boolean enabled = true;

    // Параметры в JSON (например список валют, источник и т.д.)
    @Column(length = 2048)
    private String params;

    private LocalDateTime lastRun;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = cron; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public LocalDateTime getLastRun() { return lastRun; }
    public void setLastRun(LocalDateTime lastRun) { this.lastRun = lastRun; }
}