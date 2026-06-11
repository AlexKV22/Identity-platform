package org.todo.accountservice.infrastructure.dto.inputDto;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeDtoRequest (
        @NotBlank(message = "UUID in not be a blank")
        String UUID,

        @NotBlank(message = "Verification code in not be a blank")
        String code
) {}
