package org.todo.accountservice.infrastructure.dto.redis;

public record AuthDtoRedis (
        String email,
        String password,
        Object code
) {
}
