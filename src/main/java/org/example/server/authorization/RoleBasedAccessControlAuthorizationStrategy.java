package org.example.server.authorization;

import org.example.server.model.AclRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RoleBasedAccessControlAuthorizationStrategy implements AuthorizationStrategy {
    private final Map<String, List<String>> rolePolicies = new HashMap<>();  // Role -> List of operations
    private final Map<String, List<String>> hierarchy = new HashMap<>();     // Parent Role -> Children role
    private final Map<String, String> userRoles = new HashMap<>();           // User -> Role

    public RoleBasedAccessControlAuthorizationStrategy() {
        // Initialization is now handled via the database
    }

    private void addChildRolesAccess(String role, List<String> accessControlList) {
        for (String childRole : hierarchy.getOrDefault(role, Collections.emptyList())) {
            accessControlList.addAll(rolePolicies.get(childRole));
            addChildRolesAccess(childRole, accessControlList);
        }
    }

    @Override
    public List<String> getAccess(String subject) {
        String role = userRoles.get(subject);
        if (role == null) {
            return Collections.emptyList(); // No role assigned
        }
        List<String> accessControlList = new ArrayList<>(rolePolicies.getOrDefault(role, Collections.emptyList()));
        addChildRolesAccess(role, accessControlList);
        return accessControlList;
    }

    @Override
    public void load(Connection conn) throws SQLException {
        // Load roles and policies
        try (PreparedStatement stmt = conn.prepareStatement("SELECT role, operation FROM role_policies")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                String operation = rs.getString("operation");
                rolePolicies.computeIfAbsent(role, k -> new ArrayList<>()).add(operation);
            }
        }

        // Load role hierarchy
        try (PreparedStatement stmt = conn.prepareStatement("SELECT parent_role, child_role FROM role_hierarchy")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String parentRole = rs.getString("parent_role");
                String childRole = rs.getString("child_role");
                hierarchy.computeIfAbsent(parentRole, k -> new ArrayList<>()).add(childRole);
            }
        }

        // Load user-role mappings
        try (PreparedStatement stmt = conn.prepareStatement("SELECT username, role FROM user_roles")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                userRoles.put(username, role);
            }
        }
    }

    @Override
    public void bootstrapDb(Connection conn) throws SQLException {
        // Create tables
        try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS role_policies (
                    role VARCHAR(255),
                    operation VARCHAR(255)
                )
                """)) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS role_hierarchy (
                    parent_role VARCHAR(255),
                    child_role VARCHAR(255)
                )
                """)) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS user_roles (
                    username VARCHAR(255),
                    role VARCHAR(255)
                )
                """)) {
            stmt.executeUpdate();
        }

        // Insert initial data into role_policies
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO role_policies (role, operation) VALUES (?, ?)")) {
            Map<String, List<String>> initialRolePolicies = new HashMap<>();
            initialRolePolicies.put(AclRole.MANAGER, Arrays.asList(
                    "PRINT", "QUEUE", "TOP_QUEUE", "START", "STOP", "RESTART", "STATUS", "READ_CONFIG", "SET_CONFIG"
            ));
            initialRolePolicies.put(AclRole.TECHNICIAN, Arrays.asList(
                    "START", "STOP", "RESTART", "STATUS", "READ_CONFIG", "SET_CONFIG"
            ));
            initialRolePolicies.put(AclRole.POWER_USER, Arrays.asList(
                    "TOP_QUEUE", "RESTART"
            ));
            initialRolePolicies.put(AclRole.ORDINARY_USER, Arrays.asList(
                    "PRINT", "QUEUE"
            ));

            for (Map.Entry<String, List<String>> entry : initialRolePolicies.entrySet()) {
                String role = entry.getKey();
                for (String operation : entry.getValue()) {
                    stmt.setString(1, role);
                    stmt.setString(2, operation);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }

        // Insert initial data into role_hierarchy
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO role_hierarchy (parent_role, child_role) VALUES (?, ?)")) {
            Map<String, List<String>> initialHierarchy = new HashMap<>();
            initialHierarchy.put(AclRole.MANAGER, Arrays.asList(AclRole.TECHNICIAN, AclRole.POWER_USER));
            initialHierarchy.put(AclRole.POWER_USER, Arrays.asList(AclRole.ORDINARY_USER));

            for (Map.Entry<String, List<String>> entry : initialHierarchy.entrySet()) {
                String parentRole = entry.getKey();
                for (String childRole : entry.getValue()) {
                    stmt.setString(1, parentRole);
                    stmt.setString(2, childRole);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }

        // Insert initial data into user_roles
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_roles (username, role) VALUES (?, ?)")) {
            Map<String, String> initialUserRoles = new HashMap<>();
            initialUserRoles.put("Alice", AclRole.MANAGER);
            initialUserRoles.put("Bob", AclRole.TECHNICIAN);
            initialUserRoles.put("Cecilia", AclRole.POWER_USER);
            initialUserRoles.put("David", AclRole.ORDINARY_USER);

            for (Map.Entry<String, String> entry : initialUserRoles.entrySet()) {
                stmt.setString(1, entry.getKey());
                stmt.setString(2, entry.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public boolean checkAuthorization(List<String> access, String operation) {
        return !access.contains(operation);
    }
}
