package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Optional;
import java.util.Scanner;

/**
 * Handles Register and Login, then routes the user to the menu class of their role.
 */
public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;
    private final AdminMenu adminMenu;
    private final StaffMenu staffMenu;
    private final MechanicMenu mechanicMenu;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.adminMenu = new AdminMenu(userDao, scanner);
        this.staffMenu = new StaffMenu(scanner);
        this.mechanicMenu = new MechanicMenu(scanner);
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
            System.out.println("Successfully registered a new account. Please wait for an admin to approve it.");
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
            case "APPROVED" -> route(user);
            case "PENDING" -> System.out.println("Your account is still PENDING. Please wait for an admin to approve it.");
            default -> System.out.println("Your account status is " + user.status() + ". Please contact the admin.");
        }
    }

    /** Sends the user to the menu that matches their role. */
    private void route(User user) {
        System.out.println("\nWelcome, " + user.username() + "! (" + user.roles() + ")");
        switch (user.roles()) {
            case "ADMIN" -> adminMenu.run(user);
            case "STAFF" -> staffMenu.run(user);
            case "MECHANIC" -> mechanicMenu.run(user);
            default -> System.out.println("Unknown role: " + user.roles());
        }
    }
}