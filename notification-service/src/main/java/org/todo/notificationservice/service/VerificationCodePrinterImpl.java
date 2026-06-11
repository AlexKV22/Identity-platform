package org.todo.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;

@Service
public class VerificationCodePrinterImpl implements VerificationCodePrinter {
    private final Logger logger = LoggerFactory.getLogger(VerificationCodePrinterImpl.class);
    private final NotificationMetrics notificationMetrics;

    public VerificationCodePrinterImpl(@Autowired NotificationMetrics notificationMetrics) {
        this.notificationMetrics = notificationMetrics;
    }
    @Override
    public void print(VerificationCode verificationCode) {
        logger.info("Verification code={}", verificationCode.getCode());
        notificationMetrics.incrementProcessedCounter();
    }
}
