package com.gearrentpro.entity;

import java.time.LocalDateTime;

public class Branch {
    private String branchId;
    private String name;
    private String address;
    private String contact;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Constructors
    public Branch() {}

    public Branch(String branchId, String name, String address, String contact) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.isActive = true;
    }

    // Getters and Setters
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + branchId + ")";
    }
}