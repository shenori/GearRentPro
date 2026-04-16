package com.gearrentpro.entity;

public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role;
    private String branchId;
    private boolean isActive;
    
    public enum UserRole {
        ADMIN, BRANCH_MANAGER, STAFF
    }
    
    // Constructors
    public User() {}
    
    public User(String userId, String username, String password, 
                String fullName, String email, UserRole role, 
                String branchId, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.branchId = branchId;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}