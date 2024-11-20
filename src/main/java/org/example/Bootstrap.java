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
        try (PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO users (username, hashed_password, salt) VALUES " +
                             "('Cecilia', '61KxspHv9XfcO6TO159Cb6VGvDIy5sI19DR6k6+i4lM=', '3uPcEtO7Ujw/OGoIPdiOBA==')," +
                             "('Bob', 'agdom3Kk60VO/xi3SHGrQ4xox0S30G8HQ5cf3+BHOjQ=', '3FtavKrg0Qy5wpwJcSlCFQ==')," +
                             "('David', 'KU8jk+jiCAFvSl0uyKxx+nzIBw4L4FpnXW4eU9lklrU=', '5rANVmSwYmaVzWzSKGS22g==')," +
                             "('Alice', 'BRABucAUJ2BHVy+V1g5r9x/xENj2enpr4f0RaMqUyIc=', 'Qa9C1jD60oRd3s8fN9hINw==')"
             )) {
            stmt.executeUpdate();
            System.out.println("Users created successfully");
        } catch (SQLException e) {
            System.out.println("Failed to create users." + e.getMessage());
            throw new RemoteException("Failed to create users", e);
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
