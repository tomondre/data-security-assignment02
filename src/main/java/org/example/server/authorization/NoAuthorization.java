package org.example.server.authorization;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NoAuthorization implements AuthorizationStrategy {

    @Override
    public List<String> getAccess(String subject) {
        return null;
    }

    @Override
    public void load(Connection conn) throws SQLException {
    }

    @Override
    public void bootstrapDb(Connection conn) throws SQLException {
    }

    @Override
    public boolean isNotAuthorised(List<String> access, String operation) {
        return false;
    }
}
