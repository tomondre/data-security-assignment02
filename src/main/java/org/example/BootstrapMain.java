package org.example;

import org.example.server.authorization.AclStrategy;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BootstrapMain {
    public static void main(String[] args) throws RemoteException {
        String DB_URL = "jdbc:postgresql://localhost:5432/printer_server";
        String DB_USER = "myuser";
        String DB_PASSWORD = "mypassword";

        Boolean dropDb = true;

        if (dropDb) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("DROP TABLE users")) {
                stmt.executeUpdate();
                System.out.println("User table dropped successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RemoteException("Failed to drop user table");
            }
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS users (" +
                             "id SERIAL PRIMARY KEY," +
                             "username VARCHAR(255) NOT NULL," +
                             "hashed_password VARCHAR(255) NOT NULL," +
                             "salt VARCHAR(255) NOT NULL" +
                             ")"
             )) {
            stmt.executeUpdate();
            System.out.println("User table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to create user table");
        }
        AclStrategy aclStrategy = new AclStrategy();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            aclStrategy.bootstrapDb(conn);
            System.out.println("ACL table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to create ACL table");
        }
    }
}
