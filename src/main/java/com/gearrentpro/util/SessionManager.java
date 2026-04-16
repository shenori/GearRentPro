package com.gearrentpro.util;

import com.gearrentpro.entity.User;

public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void clearSession() {
        this.currentUser = null;
    }
    
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.UserRole.ADMIN;
    }
    
    public boolean isBranchManager() {
        return currentUser != null && currentUser.getRole() == User.UserRole.BRANCH_MANAGER;
    }
    
    public boolean isStaff() {
        return currentUser != null && currentUser.getRole() == User.UserRole.STAFF;
    }
    
    public String getCurrentBranchId() {
        return currentUser != null ? currentUser.getBranchId() : null;
    }
}