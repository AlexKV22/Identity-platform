package org.todo.accountservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.todo.accountservice.infrastructure.dto.inputDto.AuthDtoRequest;
import org.todo.accountservice.infrastructure.dto.inputDto.VerificationCodeDtoRequest;
import org.todo.accountservice.infrastructure.dto.outputDto.AuthDtoResponse;
import org.todo.accountservice.service.AuthService;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(@Autowired AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user")
    public ResponseEntity<String> initAuth(@RequestBody @Valid AuthDtoRequest authDtoRequest) {
        return ResponseEntity.ok(authService.initAuth(authDtoRequest));
    }

    @PostMapping("/code")
    public ResponseEntity<AuthDtoResponse> confirmAuth(@RequestBody @Valid VerificationCodeDtoRequest verificationCodeDtoRequest) {
        return ResponseEntity.ok(authService.confirmAuth(verificationCodeDtoRequest));
    }

}
