package org.example.server.authorization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class AclStrategy implements AuthorizationStrategy {
    private final Map<String, List<String>> aclMap;

    public AclStrategy() {
        this.aclMap = new HashMap<>();
    }

    public List<String> getAccess(String subject) {
        return aclMap.get(subject);
    }

    @Override
    public void load(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM acl")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String subject = stmt.getResultSet().getString("subject");
                String operation = stmt.getResultSet().getString("operation");
                aclMap.computeIfAbsent(subject, k -> new ArrayList<>()).add(operation);
            }
        }
    }

    @Override
    public void bootstrapDb(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS acl (subject VARCHAR(255), operation VARCHAR(255))")) {
            stmt.executeUpdate();
        }

        // Insert ACL data
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO acl (subject, operation) VALUES (?, ?)")) {
            // Initial data
            Map<String, List<String>> initialAcl = new HashMap<>();
            initialAcl.put("Alice", Arrays.asList(
                    "PRINT", "QUEUE", "TOP_QUEUE", "START", "STOP", "RESTART", "STATUS", "READ_CONFIG", "SET_CONFIG"
            ));
            initialAcl.put("Bob", List.of(
                    "START", "STOP", "RESTART", "STATUS", "READ_CONFIG", "SET_CONFIG"
            ));
            initialAcl.put("Cecilia", List.of(
                    "PRINT", "QUEUE", "TOP_QUEUE", "RESTART"
            ));
            initialAcl.put("David", List.of(
                    "PRINT", "QUEUE"
            ));

            // Insert data into the table
            for (Map.Entry<String, List<String>> entry : initialAcl.entrySet()) {
                String subject = entry.getKey();
                for (String operation : entry.getValue()) {
                    stmt.setString(1, subject);
                    stmt.setString(2, operation);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch(); // Execute batch insertion
        }
    }
}
