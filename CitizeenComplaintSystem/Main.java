package CitizeenComplaintSystem;

import CitizeenComplaintSystem.repository.*;
import CitizeenComplaintSystem.service.*;
import CitizeenComplaintSystem.ui.MenuSystem;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================================");
        System.out.println("   SELECT DATABASE PERSISTENCE MODE");
        System.out.println("=================================================");
        System.out.println("1. CSV Flat Files (Default)");
        System.out.println("2. MySQL Database Server");
        System.out.print("Choose mode (1-2): ");
        String option = scanner.nextLine().trim();

        UserRepository userRepo;
        ComplaintRepository complaintRepo;

        if ("2".equals(option)) {
            System.out.println("\nInitializing MySQL Connection Repository...");
            userRepo = new MySQLUserRepository();
            complaintRepo = new MySQLComplaintRepository();
        } else {
            System.out.println("\nInitializing CSV Local Storage Files...");
            String dataDir = "data";
            File dir = new File(dataDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String usersPath = dataDir + "/users.csv";
            String complaintsPath = dataDir + "/complaints.csv";

            userRepo = new CSVUserRepository(usersPath);
            complaintRepo = new CSVComplaintRepository(complaintsPath);
        }

        AuthenticationService authService = new AuthenticationService(userRepo);
        ComplaintManager complaintManager = new ComplaintManager(complaintRepo);

        // Bootstrap UI Menu System
        MenuSystem menu = new MenuSystem(authService, complaintManager);
        menu.start();
    }
}
