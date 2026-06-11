package org.todo.accountservice.service;

import org.todo.accountservice.infrastructure.dto.inputDto.AuthDtoRequest;
import org.todo.accountservice.infrastructure.dto.inputDto.VerificationCodeDtoRequest;
import org.todo.accountservice.infrastructure.dto.outputDto.AuthDtoResponse;

public interface AuthService {
    String initAuth(AuthDtoRequest authDtoRequest);
    AuthDtoResponse confirmAuth(VerificationCodeDtoRequest verificationCodeDtoRequest);
}
