package org.example;
import org.example.common.RemoteLogin;
import org.example.common.Session;

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

        while (true) {
            try {
                String username;
                do {
                    System.out.print("Enter a username: ");
                    username = input.nextLine();
                } while (username == null || username.isEmpty());

                String password;
                do {
                    System.out.print("Enter a password: ");
                    password = input.nextLine();
                } while (password == null || password.isEmpty());

                session = model.login(username, password);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
