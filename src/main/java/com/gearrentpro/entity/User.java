package main.java.com.gearrentpro.entity;

public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String branchId;
    private String branchName;

    public User() {}

    public User(String userId, String username, String password,
                String fullName, String role, String branchId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.branchId = branchId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}
