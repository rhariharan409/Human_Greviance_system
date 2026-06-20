package CitizeenComplaintSystem.model;

import CitizeenComplaintSystem.ui.MenuSystem;

public class Citizen extends User {
    private String phoneNumber;
    private String email;
    private String address;

    public Citizen(String username, String passwordHash, String fullName, String phoneNumber, String email, String address) {
        super(username, passwordHash, fullName, "CITIZEN");
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void displayDashboard(MenuSystem menuSystem) {
        menuSystem.handleCitizenMenu();
    }
}
