package org.example.server.authorization;

import java.util.List;

public interface AuthorizationStrategy {
    List<String> getAccess(String subject);
}
