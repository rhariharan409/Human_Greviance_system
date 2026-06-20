package CitizeenComplaintSystem.ui;

import CitizeenComplaintSystem.model.*;
import CitizeenComplaintSystem.service.*;
import CitizeenComplaintSystem.util.CustomExceptions.*;

import java.util.*;

public class MenuSystem {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final ComplaintManager complaintManager;
    private final ReportGenerator reportGenerator;

    public MenuSystem(AuthenticationService authService, ComplaintManager complaintManager) {
        this.scanner = new Scanner(System.in);
        this.authService = authService;
        this.complaintManager = complaintManager;
        this.reportGenerator = new ReportGenerator(complaintManager);
    }

    public void start() {
        boolean running = true;
        while (running) {
            User current = authService.getCurrentUser();
            if (current == null) {
                running = showWelcomeMenu();
            } else {
                current.displayDashboard(this);
            }
        }
        System.out.println("Thank you for using Smart Public Complaint Priority Management System. Goodbye!");
    }

    private boolean showWelcomeMenu() {
        ConsolePrinter.printHeader("Smart Public Complaint Management System");
        System.out.println("1. Login");
        System.out.println("2. Register Citizen Account");
        System.out.println("3. View Active Priority Queue (Public Database)");
        System.out.println("4. Exit");
        ConsolePrinter.printLine();
        System.out.print("Enter Choice (1-4): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleCitizenRegistration();
                break;
            case "3":
                handleViewActiveQueue();
                break;
            case "4":
                return false;
            default:
                ConsolePrinter.printError("Invalid option. Please enter 1, 2, 3, or 4.");
        }
        return true;
    }

    private void handleLogin() {
        ConsolePrinter.printHeader("Account Login");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            User logged = authService.login(username, password);
            ConsolePrinter.printSuccess("Welcome back, " + logged.getFullName() + "! Role: " + logged.getRole());
        } catch (InvalidCredentialsException e) {
            ConsolePrinter.printError(e.getMessage());
        }
    }

    private void handleCitizenRegistration() {
        ConsolePrinter.printHeader("Citizen Self-Registration");
        System.out.print("Choose Username: ");
        String user = scanner.nextLine().trim();
        if (user.isEmpty()) {
            ConsolePrinter.printError("Username cannot be empty.");
            return;
        }

        System.out.print("Enter Password: ");
        String pass = scanner.nextLine().trim();
        if (pass.isEmpty()) {
            ConsolePrinter.printError("Password cannot be empty.");
            return;
        }

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            ConsolePrinter.printError("Full name cannot be empty.");
            return;
        }

        System.out.print("Enter Phone Number (10 digits): ");
        String phone = scanner.nextLine().trim();
        if (!phone.matches("\\d{10}")) {
            ConsolePrinter.printError("Phone number must be exactly 10 digits.");
            return;
        }

        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        if (!email.contains("@")) {
            ConsolePrinter.printError("Invalid email format.");
            return;
        }

        System.out.print("Enter Address: ");
        String address = scanner.nextLine().trim();

        try {
            authService.registerCitizen(user, pass, name, phone, email, address);
            ConsolePrinter.printSuccess("Registration successful! You can now log in.");
        } catch (UserAlreadyExistsException e) {
            ConsolePrinter.printError(e.getMessage());
        }
    }

    // =========================================================================
    // CITIZEN DASHBOARD MENU
    // =========================================================================
    public void handleCitizenMenu() {
        User logged = authService.getCurrentUser();
        ConsolePrinter.printHeader("Citizen Dashboard - " + logged.getFullName());
        System.out.println("1. File a New Complaint");
        System.out.println("2. Track My Complaints");
        System.out.println("3. Logout");
        ConsolePrinter.printLine();
        System.out.print("Enter Choice (1-3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleFileComplaint(logged.getUsername());
                break;
            case "2":
                handleTrackMyComplaints(logged.getUsername());
                break;
            case "3":
                authService.logout();
                ConsolePrinter.printSuccess("Logged out successfully.");
                break;
            default:
                ConsolePrinter.printError("Invalid option.");
        }
    }

    private void handleFileComplaint(String username) {
        ConsolePrinter.printHeader("File a New Grievance");
        
        System.out.print("Complaint Title (e.g. Garbage overflow): ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            ConsolePrinter.printError("Title is required.");
            return;
        }

        System.out.print("Detailed Description: ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) {
            ConsolePrinter.printError("Description is required.");
            return;
        }

        System.out.println("Select Category:");
        System.out.println("1. Road Damage");
        System.out.println("2. Water Leakage");
        System.out.println("3. Garbage Overflow");
        System.out.println("4. Sewage Overflow");
        System.out.println("5. Electricity Failure");
        System.out.println("6. Street Light Failure");
        System.out.println("7. Public Health Issue");
        System.out.print("Choice (1-7): ");
        String catChoice = scanner.nextLine().trim();
        String category = "Other";
        switch (catChoice) {
            case "1": category = "Road Damage"; break;
            case "2": category = "Water Leakage"; break;
            case "3": category = "Garbage Overflow"; break;
            case "4": category = "Sewage Overflow"; break;
            case "5": category = "Electricity Failure"; break;
            case "6": category = "Street Light Failure"; break;
            case "7": category = "Public Health Issue"; break;
        }

        System.out.println("Select Area / Location Ward:");
        System.out.println("1. Ward 1 (Hospital Zone - Critical Priority)");
        System.out.println("2. Ward 2 (Industrial & Manufacturing - High Priority)");
        System.out.println("3. Ward 3 (School & Civic Hub - Medium Priority)");
        System.out.println("4. Ward 4 (Commercial Sector - Moderate Priority)");
        System.out.println("5. Ward 5 (Residential - Standard Priority)");
        System.out.print("Choice (1-5): ");
        String wardChoice = scanner.nextLine().trim();
        String ward = "Ward 5 (Residential)";
        switch (wardChoice) {
            case "1": ward = "Ward 1 (Hospital)"; break;
            case "2": ward = "Ward 2 (Industrial)"; break;
            case "3": ward = "Ward 3 (School)"; break;
            case "4": ward = "Ward 4 (Commercial)"; break;
            case "5": ward = "Ward 5 (Residential)"; break;
        }

        System.out.print("Estimated Number of People Affected: ");
        int affected;
        try {
            affected = Integer.parseInt(scanner.nextLine().trim());
            if (affected < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            ConsolePrinter.printError("Invalid number. Setting affected population to 1.");
            affected = 1;
        }

        System.out.print("Rate Urgency/Severity on scale (1=Low, 10=Emergency): ");
        int severity;
        try {
            severity = Integer.parseInt(scanner.nextLine().trim());
            if (severity < 1 || severity > 10) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            ConsolePrinter.printError("Invalid severity rating. Setting to 5.");
            severity = 5;
        }

        String ticket = complaintManager.submitComplaint(title, desc, category, ward, affected, severity, username);
        ConsolePrinter.printSuccess("Grievance logged successfully! Ticket ID is: " + ticket);
    }

    private void handleTrackMyComplaints(String username) {
        ConsolePrinter.printHeader("Your Filed Grievances");
        List<Complaint> list = complaintManager.getComplaintsByCitizen(username);
        
        List<String[]> rows = new ArrayList<>();
        for (Complaint c : list) {
            rows.add(new String[]{
                    c.getComplaintId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getStatus(),
                    c.getAssignedEmployeeUsername(),
                    String.valueOf(c.getPriorityScore())
            });
        }
        ConsolePrinter.printTable(
                new String[]{"Ticket ID", "Title", "Category", "Status", "Assigned Staff", "Priority Score"},
                rows
        );
    }

    // =========================================================================
    // EMPLOYEE DASHBOARD MENU
    // =========================================================================
    public void handleEmployeeMenu() {
        Employee logged = (Employee) authService.getCurrentUser();
        ConsolePrinter.printHeader("Employee Dashboard - " + logged.getFullName() + " [" + logged.getDepartment() + " Dept]");
        System.out.println("1. View My Assigned Complaints (Sorted by Priority)");
        System.out.println("2. Update Progress on Assigned Complaint");
        System.out.println("3. Logout");
        ConsolePrinter.printLine();
        System.out.print("Enter Choice (1-3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleViewEmployeeAssigned(logged.getUsername());
                break;
            case "2":
                handleUpdateEmployeeTicket(logged.getUsername());
                break;
            case "3":
                authService.logout();
                ConsolePrinter.printSuccess("Logged out successfully.");
                break;
            default:
                ConsolePrinter.printError("Invalid option.");
        }
    }

    private void handleViewEmployeeAssigned(String username) {
        ConsolePrinter.printHeader("Your Assigned Tasks (Ranked by Urgency)");
        List<Complaint> list = complaintManager.getComplaintsByEmployee(username);
        
        List<String[]> rows = new ArrayList<>();
        for (Complaint c : list) {
            rows.add(new String[]{
                    c.getComplaintId(),
                    c.getTitle(),
                    c.getWard(),
                    c.getStatus(),
                    String.valueOf(c.getPriorityScore()),
                    String.valueOf(c.getDaysPending()) + " Days"
            });
        }
        ConsolePrinter.printTable(
                new String[]{"Ticket ID", "Title", "Ward/Area", "Status", "Priority Score", "Pending Duration"},
                rows
        );
    }

    private void handleUpdateEmployeeTicket(String username) {
        ConsolePrinter.printHeader("Update Grievance Progress");
        System.out.print("Enter Ticket ID to update (e.g. CMP-1001): ");
        String ticket = scanner.nextLine().trim().toUpperCase();

        List<Complaint> list = complaintManager.getComplaintsByEmployee(username);
        Complaint match = null;
        for (Complaint c : list) {
            if (c.getComplaintId().equalsIgnoreCase(ticket)) {
                match = c;
                break;
            }
        }

        if (match == null) {
            ConsolePrinter.printError("Ticket not found or is not assigned to you.");
            return;
        }

        System.out.println("Select New Progress State:");
        System.out.println("1. In Progress");
        System.out.println("2. Resolved / Closed");
        System.out.print("Choice (1-2): ");
        String stateChoice = scanner.nextLine().trim();

        try {
            if ("1".equals(stateChoice)) {
                complaintManager.updateStatus(ticket, "IN_PROGRESS");
                ConsolePrinter.printSuccess("Ticket status updated to IN_PROGRESS.");
            } else if ("2".equals(stateChoice)) {
                complaintManager.updateStatus(ticket, "RESOLVED");
                ConsolePrinter.printSuccess("Ticket marked as RESOLVED and closed.");
            } else {
                ConsolePrinter.printError("Invalid choice.");
            }
        } catch (Exception e) {
            ConsolePrinter.printError(e.getMessage());
        }
    }

    // =========================================================================
    // ADMIN DASHBOARD MENU
    // =========================================================================
    public void handleAdminMenu() {
        User logged = authService.getCurrentUser();
        ConsolePrinter.printHeader("Admin Central Panel - " + logged.getFullName());
        System.out.println("1. View Active Queue (Sorted dynamically by Priority Score)");
        System.out.println("2. Allocate Complaint to Field Employee");
        System.out.println("3. Register New Government Employee");
        System.out.println("4. View System Analytics & Reports");
        System.out.println("5. Increment Pending Time (Simulate 24 Hrs passage)");
        System.out.println("6. Logout");
        ConsolePrinter.printLine();
        System.out.print("Enter Choice (1-6): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleViewActiveQueue();
                break;
            case "2":
                handleAllocateComplaint();
                break;
            case "3":
                handleRegisterEmployee();
                break;
            case "4":
                reportGenerator.displayFullReport();
                break;
            case "5":
                complaintManager.incrementDaysPending();
                ConsolePrinter.printSuccess("Simulated passage of 24 Hours. Days pending incremented and priorities updated.");
                break;
            case "6":
                authService.logout();
                ConsolePrinter.printSuccess("Logged out successfully.");
                break;
            default:
                ConsolePrinter.printError("Invalid option.");
        }
    }

    private void handleViewActiveQueue() {
        ConsolePrinter.printHeader("Active Grievance Priority Heap");
        List<Complaint> activeQueue = complaintManager.getActiveQueueSorted();

        List<String[]> rows = new ArrayList<>();
        for (Complaint c : activeQueue) {
            rows.add(new String[]{
                    c.getComplaintId(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getWard(),
                    String.valueOf(c.getPriorityScore()),
                    c.getStatus(),
                    c.getAssignedEmployeeUsername()
            });
        }
        ConsolePrinter.printTable(
                new String[]{"Ticket ID", "Title", "Category", "Ward/Area", "Priority Score", "Status", "Assigned Staff"},
                rows
        );
    }

    private void handleAllocateComplaint() {
        ConsolePrinter.printHeader("Dispatch Field Staff");
        
        // Find unassigned complaints
        List<Complaint> all = complaintManager.getAllComplaints();
        List<Complaint> unassigned = new ArrayList<>();
        for (Complaint c : all) {
            if ("PENDING".equalsIgnoreCase(c.getStatus()) && "Unassigned".equalsIgnoreCase(c.getAssignedEmployeeUsername())) {
                unassigned.add(c);
            }
        }

        if (unassigned.isEmpty()) {
            ConsolePrinter.printInfo("All pending complaints have already been assigned to employees.");
            return;
        }

        System.out.println("UNASSIGNED COMPLAINTS:");
        List<String[]> compRows = new ArrayList<>();
        for (Complaint c : unassigned) {
            compRows.add(new String[]{c.getComplaintId(), c.getTitle(), c.getCategory(), String.valueOf(c.getPriorityScore())});
        }
        ConsolePrinter.printTable(new String[]{"Ticket ID", "Title", "Category", "Priority Score"}, compRows);

        System.out.print("\nEnter Complaint ID to allocate: ");
        String compId = scanner.nextLine().trim().toUpperCase();
        
        Complaint match = null;
        for (Complaint c : unassigned) {
            if (c.getComplaintId().equalsIgnoreCase(compId)) {
                match = c;
                break;
            }
        }

        if (match == null) {
            ConsolePrinter.printError("Invalid Ticket ID or ticket is already assigned.");
            return;
        }

        // Show available employees
        List<User> allUsers = authService.getAllUsers();
        List<String[]> empRows = new ArrayList<>();
        for (User u : allUsers) {
            if (u instanceof Employee) {
                Employee emp = (Employee) u;
                empRows.add(new String[]{emp.getUsername(), emp.getFullName(), emp.getDepartment(), emp.getStatus()});
            }
        }
        
        System.out.println("\nAVAILABLE FIELD STAFF:");
        ConsolePrinter.printTable(new String[]{"Username", "Full Name", "Department", "Status"}, empRows);
        
        System.out.print("\nEnter Employee Username to assign: ");
        String empUsername = scanner.nextLine().trim();
        
        // Find matching employee
        Employee targetEmp = null;
        for (User u : allUsers) {
            if (u instanceof Employee && u.getUsername().equalsIgnoreCase(empUsername)) {
                targetEmp = (Employee) u;
                break;
            }
        }
        
        if (targetEmp == null) {
            ConsolePrinter.printError("Invalid Employee Username. Allocation aborted.");
            return;
        }
        
        try {
            complaintManager.assignComplaint(compId, targetEmp.getUsername());
            ConsolePrinter.printSuccess("Ticket " + compId + " successfully assigned to " + targetEmp.getFullName());
        } catch (Exception e) {
            ConsolePrinter.printError(e.getMessage());
        }
    }


    // Placeholders for compiler safety
    private void handleRegisterEmployee() {
        ConsolePrinter.printHeader("Register Municipal Field Employee");
        System.out.print("Enter Employee Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) return;

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter Employee Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Select Assigned Department:");
        System.out.println("1. Water Infrastructure (WATER)");
        System.out.println("2. Public Sanitation & Waste (SANITATION)");
        System.out.println("3. Municipal Roads & Lights (ROADS)");
        System.out.println("4. Public Health & Hazards (HEALTH)");
        System.out.print("Choice (1-4): ");
        String depChoice = scanner.nextLine().trim();
        String department = "GENERAL";
        switch (depChoice) {
            case "1": department = "WATER"; break;
            case "2": department = "SANITATION"; break;
            case "3": department = "ROADS"; break;
            case "4": department = "HEALTH"; break;
        }

        try {
            authService.registerEmployee(username, password, name, department);
            ConsolePrinter.printSuccess("Field employee registered successfully under " + department + " department.");
        } catch (UserAlreadyExistsException e) {
            ConsolePrinter.printError(e.getMessage());
        }
    }
}
