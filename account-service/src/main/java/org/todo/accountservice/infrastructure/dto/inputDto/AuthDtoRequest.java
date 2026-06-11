package org.todo.accountservice.infrastructure.dto.inputDto;

import jakarta.validation.constraints.NotBlank;

public record AuthDtoRequest(
        @NotBlank(message = "email cannot be blank")
        String email,
        @NotBlank(message = "password cannot be blank")
        String password
) {}
