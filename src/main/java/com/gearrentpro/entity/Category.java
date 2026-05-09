package main.java.com.gearrentpro.entity;

import java.math.BigDecimal;

public class Category {
    private String categoryId;
    private String categoryName;
    private String description;
    private BigDecimal basePriceFactor;
    private BigDecimal weekendMultiplier;
    private BigDecimal lateFeePerDay;
    private boolean isActive;

    public Category() {}

    public Category(String categoryId, String categoryName, String description,
                    BigDecimal basePriceFactor, BigDecimal weekendMultiplier,
                    BigDecimal lateFeePerDay, boolean isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.basePriceFactor = basePriceFactor;
        this.weekendMultiplier = weekendMultiplier;
        this.lateFeePerDay = lateFeePerDay;
        this.isActive = isActive;
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePriceFactor() { return basePriceFactor; }
    public void setBasePriceFactor(BigDecimal basePriceFactor) { this.basePriceFactor = basePriceFactor; }

    public BigDecimal getWeekendMultiplier() { return weekendMultiplier; }
    public void setWeekendMultiplier(BigDecimal weekendMultiplier) { this.weekendMultiplier = weekendMultiplier; }

    public BigDecimal getLateFeePerDay() { return lateFeePerDay; }
    public void setLateFeePerDay(BigDecimal lateFeePerDay) { this.lateFeePerDay = lateFeePerDay; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() { return categoryName; }
}
