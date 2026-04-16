package com.gearrentpro.service;

import com.gearrentpro.dao.UserDAO;
import com.gearrentpro.entity.User;
import com.gearrentpro.util.SessionManager;

import java.sql.SQLException;

public class AuthService {
    
    private UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public User login(String username, String password) throws SQLException {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Store in session
            SessionManager.getInstance().setCurrentUser(user);
            return user;
        }
        
        return null;
    }
    
    public void logout() {
        SessionManager.getInstance().clearSession();
    }
    
    public User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }
    
    public boolean isLoggedIn() {
        return SessionManager.getInstance().getCurrentUser() != null;
    }
    
    public boolean hasRole(User.UserRole requiredRole) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        return currentUser.getRole() == requiredRole;
    }
}