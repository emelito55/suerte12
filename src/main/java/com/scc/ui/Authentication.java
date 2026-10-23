package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Handles Register, Login and Logout, then routes the user to the menu of their role.
 */
public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=====================================");
            System.out.println("   MOTORENT: MOTORCYCLE RENTAL SYSTEM");
            System.out.println("=====================================");
            System.out.println("[1] Register Account");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Thank you for using MotoRent. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2 or 3.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- REGISTER ACCOUNT ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken. Please choose another one.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.println("Select role:");
        System.out.println("[1] ADMIN");
        System.out.println("[2] STAFF");
        System.out.println("[3] MECHANIC");
        System.out.print("Choose: ");
        String roles;
        switch (scanner.nextLine().trim()) {
            case "1" -> roles = "ADMIN";
            case "2" -> roles = "STAFF";
            case "3" -> roles = "MECHANIC";
            default -> {
                System.out.println("Invalid role. Registration cancelled.");
                return;
            }
        }

        if (userDao.registerUser(username, password, roles)) {
            System.out.println("Successfully registering a new account.");
        }
    }

    private void login() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<User> found = userDao.login(username, password);
        if (found.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }

        User user = found.get();
        switch (user.status()) {
            case "ACTIVE" -> route(user);
            case "PENDING" -> System.out.println("Your account is still PENDING. Please wait for an admin to approve it.");
            default -> System.out.println("Your account status is " + user.status() + ". Please contact the admin.");
        }
    }

    /** Sends the user to the menu that matches their role. */
    private void route(User user) {
        System.out.println("\nWelcome, " + user.username() + "! (" + user.roles() + ")");
        switch (user.roles()) {
            case "ADMIN" -> adminMenu(user);
            case "STAFF" -> staffMenu(user);
            case "MECHANIC" -> mechanicMenu(user);
            default -> System.out.println("Unknown role: " + user.roles());
        }
    }

    private void adminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("[1] Review Pending Accounts");
            System.out.println("[2] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> reviewPendingAccounts();
                case "2" -> loggedIn = logout();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void reviewPendingAccounts() {
        List<User> pending = userDao.findByStatus("PENDING");
        if (pending.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }

        System.out.println("\nPending accounts:");
        for (User u : pending) {
            System.out.println(u);
        }
        System.out.print("Enter user ID to review (0 to cancel): ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        if (id == 0) {
            return;
        }
        if (pending.stream().noneMatch(u -> u.id() == id)) {
            System.out.println("That ID is not in the pending list.");
            return;
        }

        System.out.print("[1] Approve  [2] Reject: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                if (userDao.updateStatus(id, "ACTIVE")) System.out.println("Account approved.");
            }
            case "2" -> {
                if (userDao.updateStatus(id, "REJECTED")) System.out.println("Account rejected.");
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void staffMenu(User staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- STAFF MENU ---");
            System.out.println("[1] Rentals (coming soon)");
            System.out.println("[2] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> System.out.println("Rental features will be added in the next activity.");
                case "2" -> loggedIn = logout();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void mechanicMenu(User mechanic) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- MECHANIC MENU ---");
            System.out.println("[1] Motorcycle Maintenance (coming soon)");
            System.out.println("[2] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> System.out.println("Maintenance features will be added in the next activity.");
                case "2" -> loggedIn = logout();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    /** Ends the session. Returns false so the menu loop stops. */
    private boolean logout() {
        System.out.println("You have been logged out.");
        return false;
    }
}