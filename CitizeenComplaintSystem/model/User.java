package CitizeenComplaintSystem.model;

import CitizeenComplaintSystem.ui.MenuSystem;

public abstract class User {
    protected String username;
    protected String passwordHash;
    protected String fullName;
    protected String role; // "CITIZEN", "EMPLOYEE", "ADMIN"

    public User(String username, String passwordHash, String fullName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean checkPassword(String rawPassword) {
        // Simple base64 or custom encoding comparison to mock password hashing
        String encoded = java.util.Base64.getEncoder().encodeToString(rawPassword.getBytes());
        return this.passwordHash.equals(encoded);
    }

    public abstract void displayDashboard(MenuSystem menuSystem);
}
