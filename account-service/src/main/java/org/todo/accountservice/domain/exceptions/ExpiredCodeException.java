package org.todo.accountservice.domain.exceptions;

public class ExpiredCodeException extends RuntimeException {
    public ExpiredCodeException(String message) {
        super(message);
    }
}
