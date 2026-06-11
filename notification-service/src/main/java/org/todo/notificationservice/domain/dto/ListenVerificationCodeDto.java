package org.todo.notificationservice.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ListenVerificationCodeDto(
        @NotNull(message = "EventId is null")
        UUID eventId,
        @NotBlank(message = "Email is blank")
        String email,
        @Positive(message = "Verification code can be only positive")
        @Max(message = "Verification code have value max 9999", value = 9999)
        @Min(message = "Verification code have value min 1000", value = 1000)
        Integer code
) {}
