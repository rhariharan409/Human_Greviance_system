package CitizeenComplaintSystem.repository;

import CitizeenComplaintSystem.model.User;
import CitizeenComplaintSystem.model.Citizen;
import CitizeenComplaintSystem.model.Employee;
import CitizeenComplaintSystem.model.Admin;
import CitizeenComplaintSystem.util.CSVHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVUserRepository implements UserRepository {
    private final String filePath;
    private final Map<String, User> userCache = new LinkedHashMap<>();

    public CSVUserRepository(String filePath) {
        this.filePath = filePath;
        initFile();
        loadAll();
    }

    private void initFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                // Ensure parent directory exists
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                // Pre-populate with default profiles
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println("username,passwordHash,fullName,role,phoneNumber,email,address,department,statusOrLevel");
                    
                    // Admin: username "admin", password "admin123" -> Base64 "YWRtaW4xMjM="
                    pw.println(CSVHelper.toCSVRow("admin", "YWRtaW4xMjM=", "System Administrator", "ADMIN", "", "", "", "", "1"));
                    
                    // Employee 1: username "emp1", password "emp123" -> Base64 "ZW1wMTIz"
                    pw.println(CSVHelper.toCSVRow("emp1", "ZW1wMTIz=", "John Doe (Water Dept)", "EMPLOYEE", "", "", "", "WATER", "ACTIVE"));
                    
                    // Employee 2: username "emp2", password "emp123" -> Base64 "ZW1wMTIz"
                    pw.println(CSVHelper.toCSVRow("emp2", "ZW1wMTIz=", "Jane Smith (Sanitation Dept)", "EMPLOYEE", "", "", "", "SANITATION", "ACTIVE"));
                    
                    // Citizen: username "citizen", password "cit123" -> Base64 "Y2l0MTIz"
                    pw.println(CSVHelper.toCSVRow("citizen", "Y2l0MTIz=", "Alice Brown", "CITIZEN", "9876543210", "alice@gmail.com", "12 Ward Road", "", ""));
                }
            } catch (IOException e) {
                System.err.println("Error initializing users file: " + e.getMessage());
            }
        }
    }

    private void loadAll() {
        userCache.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = CSVHelper.parseCSVRow(line);
                if (fields.size() < 9) continue;
                
                String username = fields.get(0);
                String passwordHash = fields.get(1);
                String fullName = fields.get(2);
                String role = fields.get(3);
                String phoneNumber = fields.get(4);
                String email = fields.get(5);
                String address = fields.get(6);
                String department = fields.get(7);
                String statusOrLevel = fields.get(8);

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
                    userCache.put(username, user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
    }

    private void flushToDisk() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("username,passwordHash,fullName,role,phoneNumber,email,address,department,statusOrLevel");
            for (User u : userCache.values()) {
                String phone = "";
                String email = "";
                String address = "";
                String dept = "";
                String statusOrLvl = "";

                if (u instanceof Citizen) {
                    Citizen c = (Citizen) u;
                    phone = c.getPhoneNumber();
                    email = c.getEmail();
                    address = c.getAddress();
                } else if (u instanceof Employee) {
                    Employee e = (Employee) u;
                    dept = e.getDepartment();
                    statusOrLvl = e.getStatus();
                } else if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    statusOrLvl = String.valueOf(a.getAdminLevel());
                }

                pw.println(CSVHelper.toCSVRow(
                        u.getUsername(),
                        u.getPasswordHash(),
                        u.getFullName(),
                        u.getRole(),
                        phone,
                        email,
                        address,
                        dept,
                        statusOrLvl
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing to users file: " + e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        userCache.put(user.getUsername(), user);
        flushToDisk();
    }

    @Override
    public User findByUsername(String username) {
        return userCache.get(username);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userCache.values());
    }
}
