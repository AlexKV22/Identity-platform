package org.todo.accountservice.infrastructure.dto.kafka;

import java.util.UUID;

public record SendVerificationCodeDto(
        UUID eventId,
        String email,
        Integer code
) {}
