package org.todo.accountservice.infrastructure.dto.outputDto;


public record AuthDtoResponse(
        Long id,
        String email,
        String token,
        String refreshToken
) {

}
