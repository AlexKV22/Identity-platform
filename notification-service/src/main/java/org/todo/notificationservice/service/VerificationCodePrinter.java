package org.todo.notificationservice.service;

import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;

public interface VerificationCodePrinter {
    void print(VerificationCode verificationCode);
}
