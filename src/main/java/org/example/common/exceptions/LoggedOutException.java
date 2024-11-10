package org.example.common.exceptions;

public class LoggedOutException extends Exception {
    public LoggedOutException(String username) {
        super("Exception: User: " + username + " logged out");
    }
}
