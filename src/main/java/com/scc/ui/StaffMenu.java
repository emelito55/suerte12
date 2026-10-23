package com.scc.ui;

import com.scc.model.User;

import java.util.Scanner;

/** Menu for STAFF accounts. */
public class StaffMenu {

    private final Scanner scanner;

    public StaffMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Runs until the staff member logs out. */
    public void run(User staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- STAFF MENU (" + staff.username() + ") ---");
            System.out.println("[1] Rentals (coming soon)");
            System.out.println("[2] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> System.out.println("Rental features will be added in the next activity.");
                case "2" -> {
                    System.out.println("You have been logged out.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}