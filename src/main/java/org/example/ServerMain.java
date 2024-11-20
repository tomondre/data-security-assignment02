package org.example;


import org.example.server.Server;
import org.example.server.authorization.AuthorizationStrategy;
import org.example.server.authorization.RoleBasedAccessControlAuthorizationStrategy;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, SQLException {
        boolean bootstrap = true;

        AuthorizationStrategy authorizationStrategy = new RoleBasedAccessControlAuthorizationStrategy();
//        AuthorizationStrategy authorizationStrategy = new AclStrategy();
//        AuthorizationStrategy authorizationStrategy = new NoAuthorization();

        String DB_URL = "jdbc:postgresql://localhost:5432/printer_server";
        String DB_USER = "myuser";
        String DB_PASSWORD = "mypassword";

        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        if (bootstrap) {
            new Bootstrap(authorizationStrategy, connection).bootstrap();
        }
        new Server(authorizationStrategy, connection);
    }
}
