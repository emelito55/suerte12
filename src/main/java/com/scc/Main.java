
package com.scc;
import com.scc.dao.UserDao;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao UserDao = new UserDao();

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("MotorCycleRentalSystem");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.println("==================================");
        System.out.print("Choose an option: ");

        String choice = sc.nextLine();

        switch (choice) {

            case "1":
                System.out.println("Login selected.");
                break;

            case "2":
                System.out.print("Enter username: ");
                String user = sc.nextLine();

                System.out.print("Enter password: ");
                String pass = sc.nextLine();

                System.out.println("Select role [1] USER | [2] ADMIN");
                System.out.print("Enter your choice: ");

                int roleChoice;

                try {
                    roleChoice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid role selection!");
                    break;
                }

                String role;

                switch (roleChoice) {
                    case 1:
                        role = "USER";
                        break;

                    case 2:
                        role = "ADMIN";
                        break;

                    default:
                        role = null;
                        break;
                }

                if (role != null) {
                    UserDao.registerUser(user, pass, role);
                } else {
                    System.out.println("Invalid role selection!");
                }

                break;

            case "3":
                System.out.println("Thank you for using MotorCycleRentalSystem!");
                break;

            default:
                System.out.println("Invalid option!");
                break;
        }

        sc.close();
    }
}
