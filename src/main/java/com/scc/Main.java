package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (!DatabaseInitializer.initialize()) {
            System.out.println("Cannot start the application without a database.");
            return;
        }
        System.out.println("Database file: " + new File("MotorRental.db").getAbsolutePath());

        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        new Authentication(userDao, scanner).start();
    }
}