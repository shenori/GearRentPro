package main.java.com.gearrentpro.util;

import main.java.com.gearrentpro.entity.User;

public class SessionManager {

    private static SessionManager instance;
    private User loggedInUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public String getUserRole() {
        if (loggedInUser != null) {
            return loggedInUser.getRole();
        }
        return null;
    }

    public String getUserBranchId() {
        if (loggedInUser != null) {
            return loggedInUser.getBranchId();
        }
        return null;
    }

    public boolean isAdmin() {
        return "Admin".equals(getUserRole());
    }

    public boolean isBranchManager() {
        return "Branch Manager".equals(getUserRole());
    }

    public boolean isStaff() {
        return "Staff".equals(getUserRole());
    }

    public void logout() {
        loggedInUser = null;
    }
}
