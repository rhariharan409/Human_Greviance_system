package CitizeenComplaintSystem.service;

import CitizeenComplaintSystem.model.User;
import CitizeenComplaintSystem.model.Citizen;
import CitizeenComplaintSystem.model.Employee;
import CitizeenComplaintSystem.repository.UserRepository;
import CitizeenComplaintSystem.util.CustomExceptions.*;

import java.util.Base64;

public class AuthenticationService {
    private final UserRepository userRepo;
    private User currentUser;

    public AuthenticationService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public synchronized User login(String username, String password) throws InvalidCredentialsException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new InvalidCredentialsException("Username '" + username + "' not found.");
        }
        if (!user.checkPassword(password)) {
            throw new InvalidCredentialsException("Incorrect password. Please try again.");
        }
        this.currentUser = user;
        return user;
    }

    public synchronized void registerCitizen(String username, String rawPassword, String fullName, 
                                            String phoneNumber, String email, String address) throws UserAlreadyExistsException {
        if (userRepo.findByUsername(username) != null) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
        }

        // Encode password to Base64 (simulating hash)
        String passwordHash = Base64.getEncoder().encodeToString(rawPassword.getBytes());
        
        Citizen newCitizen = new Citizen(username, passwordHash, fullName, phoneNumber, email, address);
        userRepo.save(newCitizen);
    }

    public synchronized void registerEmployee(String username, String rawPassword, String fullName, 
                                             String department) throws UserAlreadyExistsException {
        if (userRepo.findByUsername(username) != null) {
            throw new UserAlreadyExistsException("Username '" + username + "' is already taken.");
        }

        // Encode password
        String passwordHash = Base64.getEncoder().encodeToString(rawPassword.getBytes());
        
        Employee newEmployee = new Employee(username, passwordHash, fullName, department, "ACTIVE");
        userRepo.save(newEmployee);
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized java.util.List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public synchronized void logout() {
        this.currentUser = null;
    }
}

