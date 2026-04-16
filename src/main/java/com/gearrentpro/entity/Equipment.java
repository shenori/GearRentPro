package com.gearrentpro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Equipment {
    private String equipmentId;
    private String categoryId;
    private String branchId;
    private String brand;
    private String model;
    private int purchaseYear;
    private BigDecimal dailyBasePrice;
    private BigDecimal securityDeposit;
    private EquipmentStatus status;
    private String description;
    private LocalDateTime createdAt;

    // For display purposes (joined data)
    private String categoryName;
    private String branchName;

    public enum EquipmentStatus {
        AVAILABLE("Available"),
        RESERVED("Reserved"),
        RENTED("Rented"),
        UNDER_MAINTENANCE("Under Maintenance");

        private final String displayName;

        EquipmentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Equipment() {
        this.status = EquipmentStatus.AVAILABLE;
    }

    // Getters and Setters
    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getPurchaseYear() { return purchaseYear; }
    public void setPurchaseYear(int purchaseYear) { this.purchaseYear = purchaseYear; }

    public BigDecimal getDailyBasePrice() { return dailyBasePrice; }
    public void setDailyBasePrice(BigDecimal dailyBasePrice) { this.dailyBasePrice = dailyBasePrice; }

    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    // Helper method for display
    public String getFullName() {
        return brand + " " + model;
    }

    @Override
    public String toString() {
        return brand + " " + model + " (" + equipmentId + ")";
    }
}