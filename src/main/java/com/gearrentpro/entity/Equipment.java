package main.java.com.gearrentpro.entity;

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
    private String status;
    private String categoryName;
    private String branchName;

    public Equipment() {}

    public Equipment(String equipmentId, String categoryId, String branchId, String brand,
                     String model, int purchaseYear, BigDecimal dailyBasePrice,
                     BigDecimal securityDeposit, String status) {
        this.equipmentId = equipmentId;
        this.categoryId = categoryId;
        this.branchId = branchId;
        this.brand = brand;
        this.model = model;
        this.purchaseYear = purchaseYear;
        this.dailyBasePrice = dailyBasePrice;
        this.securityDeposit = securityDeposit;
        this.status = status;
    }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    @Override
    public String toString() { return brand + " " + model; }
}
