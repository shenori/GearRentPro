package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Category {
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal basePriceFactor;
    private BigDecimal weekendMultiplier;
    private BigDecimal defaultLateFeePerDay;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Constructors
    public Category() {
        this.basePriceFactor = BigDecimal.ONE;
        this.weekendMultiplier = BigDecimal.ONE;
        this.defaultLateFeePerDay = new BigDecimal("500.00");
        this.isActive = true;
    }

    public Category(String categoryId, String name, String description,
                    BigDecimal basePriceFactor, BigDecimal weekendMultiplier,
                    BigDecimal defaultLateFeePerDay) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.basePriceFactor = basePriceFactor;
        this.weekendMultiplier = weekendMultiplier;
        this.defaultLateFeePerDay = defaultLateFeePerDay;
        this.isActive = true;
    }

    // Getters and Setters
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePriceFactor() { return basePriceFactor; }
    public void setBasePriceFactor(BigDecimal basePriceFactor) { this.basePriceFactor = basePriceFactor; }

    public BigDecimal getWeekendMultiplier() { return weekendMultiplier; }
    public void setWeekendMultiplier(BigDecimal weekendMultiplier) { this.weekendMultiplier = weekendMultiplier; }

    public BigDecimal getDefaultLateFeePerDay() { return defaultLateFeePerDay; }
    public void setDefaultLateFeePerDay(BigDecimal defaultLateFeePerDay) { this.defaultLateFeePerDay = defaultLateFeePerDay; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name;
    }
}