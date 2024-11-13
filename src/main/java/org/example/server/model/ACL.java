package org.example.server.model;

import java.util.*;

public class ACL {
    private final Map<String, List<String>> aclMap;

    public ACL() {
        this.aclMap = new HashMap<>();
        this.aclMap.put("Alice", Arrays.asList(AclOperation.PRINT, AclOperation.QUEUE, AclOperation.TOP_QUEUE, AclOperation.START, AclOperation.STOP, AclOperation.RESTART, AclOperation.STATUS, AclOperation.READ_CONFIG, AclOperation.SET_CONFIG));
        this.aclMap.put("Bob", List.of(AclOperation.START, AclOperation.STOP, AclOperation.RESTART, AclOperation.STATUS, AclOperation.READ_CONFIG, AclOperation.SET_CONFIG));
        this.aclMap.put("Cecilia", List.of(AclOperation.PRINT, AclOperation.QUEUE, AclOperation.TOP_QUEUE, AclOperation.RESTART));
        this.aclMap.put("David", List.of(AclOperation.PRINT, AclOperation.QUEUE));
    }

    public List<String> getRights(String subject) {
        return aclMap.get(subject);
    }
}
