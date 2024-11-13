package org.example.common.exceptions;

public class UnauthorisedException
    extends Exception {
    public UnauthorisedException(String username) {
        super("Exception: User: " + username + " is not authorised to perform this operation");
    }
}
