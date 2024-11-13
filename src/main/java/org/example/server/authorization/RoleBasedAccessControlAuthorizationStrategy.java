package org.example.server.authorization;

import org.example.server.model.AclRole;

import java.util.*;

public class RoleBasedAccessControlAuthorizationStrategy implements AuthorizationStrategy {
    private final Map<String, List<String>> rolePolicies = new HashMap<>();  // Role -> List of operations
    private final Map<String, List<String>> hierarchy = new HashMap<>();     // Parent Role -> Children role
    private final Map<String, String> userRoles = new HashMap<>();           // User -> Role

    public RoleBasedAccessControlAuthorizationStrategy() {
        this.rolePolicies.put(AclRole.MANAGER, new ArrayList<String>());
        this.rolePolicies.put(AclRole.TECHNICIAN, List.of(Operation.START, Operation.STOP, Operation.RESTART, Operation.STATUS, Operation.READ_CONFIG, Operation.SET_CONFIG));
        this.rolePolicies.put(AclRole.POWER_USER, List.of(Operation.TOP_QUEUE, Operation.RESTART));
        this.rolePolicies.put(AclRole.ORDINARY_USER, List.of(Operation.PRINT, Operation.QUEUE));

        this.hierarchy.put(AclRole.MANAGER, Arrays.asList(AclRole.TECHNICIAN, AclRole.POWER_USER));
        this.hierarchy.put(AclRole.POWER_USER, List.of(AclRole.ORDINARY_USER));

        this.userRoles.put("Alice", AclRole.MANAGER);
        this.userRoles.put("Bob", AclRole.TECHNICIAN);
        this.userRoles.put("Cecilia", AclRole.POWER_USER);
        this.userRoles.put("David", AclRole.ORDINARY_USER);
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
        List<String> accessControlList = new ArrayList<>(rolePolicies.get(role));
        addChildRolesAccess(role, accessControlList);
        return accessControlList;
    }
}
