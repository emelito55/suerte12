package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

/**
 * Menu for ADMIN accounts: view all users, view pending registrations, approve accounts.
 */
public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    /** Runs until the admin logs out. */
    public void run(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- ADMIN MENU (" + admin.username() + ") ---");
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingRegistrations();
                case "3" -> approveUserAccount();
                case "4" -> {
                    System.out.println("You have been logged out.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, 3 or 4.");
            }
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.println("\nALL SYSTEM USERS");
        printUserTable(users);
    }

    private void viewPendingRegistrations() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }
        System.out.println("\nPENDING REGISTRATIONS");
        printUserTable(pending);
    }

    private void approveUserAccount() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending registrations to approve.");
            return;
        }

        System.out.println("\nPENDING REGISTRATIONS");
        printUserTable(pending);

        System.out.print("Enter Target User ID to approve (0 to cancel): ");
        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
            return;
        }

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }
        if (pending.stream().noneMatch(u -> u.id() == userId)) {
            System.out.println("User ID " + userId + " is not in the pending list.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("Success! User ID " + userId + " has been APPROVED.");
        } else {
            System.out.println("Could not approve user ID " + userId + ".");
        }
    }

    /** Prints any list of users as an aligned table with borders. */
    private void printUserTable(List<User> users) {
        String border = "+-" + "-".repeat(5) + "-+-" + "-".repeat(15) + "-+-" + "-".repeat(12) + "-+-" + "-".repeat(10) + "-+";
        String rowFormat = "| %-5s | %-15s | %-12s | %-10s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        for (User u : users) {
            System.out.printf(rowFormat, u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
        System.out.println("Total: " + users.size());
    }
}