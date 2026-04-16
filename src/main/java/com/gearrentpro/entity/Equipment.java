package com.gearrentpro.entity;

import java.math.BigDecimal;

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
    
    public enum EquipmentStatus {
        AVAILABLE, RESERVED, RENTED, UNDER_MAINTENANCE
    }
    
    // Constructors
    public Equipment() {}
    
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
}