package org.example.common.exceptions;

public class InvalidJwtException extends Exception {
    public InvalidJwtException() {
        super("Exception: Invalid JWT");
    }
}
