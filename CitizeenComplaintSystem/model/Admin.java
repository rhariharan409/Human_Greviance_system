package CitizeenComplaintSystem.model;

import CitizeenComplaintSystem.ui.MenuSystem;

public class Admin extends User {
    private int adminLevel;

    public Admin(String username, String passwordHash, String fullName, int adminLevel) {
        super(username, passwordHash, fullName, "ADMIN");
        this.adminLevel = adminLevel;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public void displayDashboard(MenuSystem menuSystem) {
        menuSystem.handleAdminMenu();
    }
}
