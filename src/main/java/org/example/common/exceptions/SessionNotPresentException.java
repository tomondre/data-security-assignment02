package org.example.common.exceptions;

public class SessionNotPresentException extends Exception {
    public SessionNotPresentException() {
        super("Exception: Session not present");
    }
}
