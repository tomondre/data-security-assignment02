package org.example.server.authorization;

import java.util.*;

public class AclStrategy implements AuthorizationStrategy {
    private final Map<String, List<String>> aclMap;

    public AclStrategy() {
        this.aclMap = new HashMap<>();
        this.aclMap.put("Alice", Arrays.asList(Operation.PRINT, Operation.QUEUE, Operation.TOP_QUEUE, Operation.START, Operation.STOP, Operation.RESTART, Operation.STATUS, Operation.READ_CONFIG, Operation.SET_CONFIG));
        this.aclMap.put("Bob", List.of(Operation.START, Operation.STOP, Operation.RESTART, Operation.STATUS, Operation.READ_CONFIG, Operation.SET_CONFIG));
        this.aclMap.put("Cecilia", List.of(Operation.PRINT, Operation.QUEUE, Operation.TOP_QUEUE, Operation.RESTART));
        this.aclMap.put("David", List.of(Operation.PRINT, Operation.QUEUE));
    }

    public List<String> getAccess(String subject) {
        return aclMap.get(subject);
    }
}
