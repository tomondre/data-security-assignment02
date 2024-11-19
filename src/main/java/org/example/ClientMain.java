package org.example;

import org.example.common.RemoteLogin;
import org.example.common.Session;
import org.example.server.model.Password;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws NotBoundException {
        Scanner input = new Scanner(System.in);
        RemoteLogin model = null;
        Session session = null;
        try {
            model = (RemoteLogin) Naming.lookup("rmi://localhost:1099/Login");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Welcome to the Printer Server");
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.print("Select an option: ");
            String option = input.nextLine();

            if ("1".equals(option)) {
                register(model, input);
            } else if ("2".equals(option)) {
                session = login(model, input);
                if (session != null) {
                    tryStart(model, session);
                }
            } else {
                System.out.println("Invalid option");
            }
        }
    }

    public static void register(RemoteLogin model, Scanner input) {
        try {
            String username;
            do {
                System.out.print("Enter a username: ");
                username = input.nextLine();
            } while (username == null || username.isEmpty());

            String passwordStr;
            do {
                System.out.print("Enter a password: ");
                passwordStr = input.nextLine();
            } while (passwordStr == null || passwordStr.isEmpty());

            // Create Password object to hash and salt the password
            Password password = new Password(passwordStr);
            String hashedPassword = password.getHashedPassword();
            String salt = password.getSalt();

            // Send username, hashedPassword, and salt to the server to register
            model.register(username, hashedPassword, salt);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    public static Session login(RemoteLogin model, Scanner input) {
        try {
            String username;
            do {
                System.out.print("Enter your username: ");
                username = input.nextLine();
            } while (username == null || username.isEmpty());

            // Request the salt from the server
            String salt = model.requestSalt(username);

            String passwordStr;
            do {
                System.out.print("Enter your password: ");
                passwordStr = input.nextLine();
            } while (passwordStr == null || passwordStr.isEmpty());

            // Hash the password using the retrieved salt
            String hashedPassword = Password.hashPassword(passwordStr, salt);

            // Send username and hashedPassword to the server to login
            Session session = model.login(username, hashedPassword);
            System.out.println("Login successful.");
            return session;
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }

    public static void tryStart(RemoteLogin model, Session session) {
        try {
            model.start(session);
            System.out.println("Server started successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
