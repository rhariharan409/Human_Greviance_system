package CitizeenComplaintSystem.model;

import CitizeenComplaintSystem.ui.MenuSystem;

public class Employee extends User {
    private String department; // e.g. "WATER", "ROADS", "GARBAGE", "HEALTH"
    private String status;     // "ACTIVE", "ON_LEAVE"

    public Employee(String username, String passwordHash, String fullName, String department, String status) {
        super(username, passwordHash, fullName, "EMPLOYEE");
        this.department = department;
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void displayDashboard(MenuSystem menuSystem) {
        menuSystem.handleEmployeeMenu();
    }
}
