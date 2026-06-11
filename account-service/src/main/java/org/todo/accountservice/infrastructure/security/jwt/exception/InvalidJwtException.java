package org.todo.accountservice.infrastructure.security.jwt.exception;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException() {
        super("Неверный JWT токен при валидации");
    }
}
