package org.todo.accountservice.infrastructure.verificationcodesender;

public interface VerificationCodeSender {
    void send(String email, Integer code);
}
