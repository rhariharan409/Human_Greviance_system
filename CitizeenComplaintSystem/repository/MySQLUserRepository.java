package CitizeenComplaintSystem.repository;

import CitizeenComplaintSystem.model.*;
import CitizeenComplaintSystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password_hash, full_name, role, phone_number, email, address, department, status_or_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE users SET password_hash = ?, full_name = ?, role = ?, phone_number = ?, email = ?, address = ?, department = ?, status_or_level = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, user.getUsername());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            String phone = "";
            String email = "";
            String address = "";
            String dept = "";
            String statusOrLvl = "";

            if (user instanceof Citizen) {
                Citizen c = (Citizen) user;
                phone = c.getPhoneNumber();
                email = c.getEmail();
                address = c.getAddress();
            } else if (user instanceof Employee) {
                Employee e = (Employee) user;
                dept = e.getDepartment();
                statusOrLvl = e.getStatus();
            } else if (user instanceof Admin) {
                Admin a = (Admin) user;
                statusOrLvl = String.valueOf(a.getAdminLevel());
            }

            if (exists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, user.getPasswordHash());
                    updateStmt.setString(2, user.getFullName());
                    updateStmt.setString(3, user.getRole());
                    updateStmt.setString(4, phone);
                    updateStmt.setString(5, email);
                    updateStmt.setString(6, address);
                    updateStmt.setString(7, dept);
                    updateStmt.setString(8, statusOrLvl);
                    updateStmt.setString(9, user.getUsername());
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, user.getUsername());
                    insertStmt.setString(2, user.getPasswordHash());
                    insertStmt.setString(3, user.getFullName());
                    insertStmt.setString(4, user.getRole());
                    insertStmt.setString(5, phone);
                    insertStmt.setString(6, email);
                    insertStmt.setString(7, address);
                    insertStmt.setString(8, dept);
                    insertStmt.setString(9, statusOrLvl);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error saving user: " + e.getMessage());
        }
    }

    @Override
    public User findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("password_hash");
                    String fullName = rs.getString("full_name");
                    String role = rs.getString("role");
                    String phoneNumber = rs.getString("phone_number");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String department = rs.getString("department");
                    String statusOrLevel = rs.getString("status_or_level");

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        int level = 1;
                        try {
                            level = Integer.parseInt(statusOrLevel);
                        } catch (NumberFormatException ignored) {}
                        return new Admin(username, passwordHash, fullName, level);
                    } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
                        return new Employee(username, passwordHash, fullName, department, statusOrLevel);
                    } else if ("CITIZEN".equalsIgnoreCase(role)) {
                        return new Citizen(username, passwordHash, fullName, phoneNumber, email, address);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error finding user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String fullName = rs.getString("full_name");
                String role = rs.getString("role");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String department = rs.getString("department");
                String statusOrLevel = rs.getString("status_or_level");

                User user = null;
                if ("ADMIN".equalsIgnoreCase(role)) {
                    int level = 1;
                    try {
                        level = Integer.parseInt(statusOrLevel);
                    } catch (NumberFormatException ignored) {}
                    user = new Admin(username, passwordHash, fullName, level);
                } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
                    user = new Employee(username, passwordHash, fullName, department, statusOrLevel);
                } else if ("CITIZEN".equalsIgnoreCase(role)) {
                    user = new Citizen(username, passwordHash, fullName, phoneNumber, email, address);
                }

                if (user != null) {
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error loading all users: " + e.getMessage());
        }
        return list;
    }
}
