package org.example.server.authorization;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AuthorizationStrategy {
    List<String> getAccess(String subject);
    void load(Connection conn) throws SQLException;
    void bootstrapDb(Connection conn) throws SQLException;
}
