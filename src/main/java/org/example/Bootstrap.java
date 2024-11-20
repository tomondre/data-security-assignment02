package org.example;

import org.example.server.authorization.AuthorizationStrategy;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bootstrap {

    private final AuthorizationStrategy authorizationStrategy;
    private final Connection connection;

    public Bootstrap(AuthorizationStrategy authorizationStrategy, Connection connection) {
        this.authorizationStrategy = authorizationStrategy;
        this.connection = connection;
    }

    public void bootstrap() throws RemoteException {
        Boolean dropDb = true;

//        if (dropDb) {
//            try (Connection conn = connection;
//                 PreparedStatement stmt = conn.prepareStatement("DROP database printer_server")) {
//                stmt.executeUpdate();
//                System.out.println("Database dropped successfully");
//                PreparedStatement stmt2 = conn.prepareStatement("CREATE database printer_server");
//                stmt2.executeUpdate();
//                System.out.println("Database created successfully");
//
//            } catch (SQLException e) {
//                System.out.println("Failed to drop database." + e.getMessage());
//                throw new RemoteException("Failed to drop user table");
//            }
//        }

        try (PreparedStatement stmt = connection.prepareStatement(
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
            System.out.println("Failed to create user table." + e.getMessage());
            throw new RemoteException("Failed to create user table", e);
        }
        try {
            this.authorizationStrategy.bootstrapDb(connection);
            System.out.println("ACL table created successfully");
        } catch (SQLException e) {
            System.out.println("Failed to create ACL table." + e.getMessage());
            throw new RemoteException("Failed to create ACL table", e);
        }
    }
}
