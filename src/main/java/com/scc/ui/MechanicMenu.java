package com.scc.ui;

import com.scc.model.User;

import java.util.Scanner;

/** Menu for MECHANIC accounts. */
public class MechanicMenu {

    private final Scanner scanner;

    public MechanicMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Runs until the mechanic logs out. */
    public void run(User mechanic) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- MECHANIC MENU (" + mechanic.username() + ") ---");
            System.out.println("[1] Motorcycle Maintenance (coming soon)");
            System.out.println("[2] Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> System.out.println("Maintenance features will be added in the next activity.");
                case "2" -> {
                    System.out.println("You have been logged out.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}