package org.todo.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;

@Service
public class VerificationCodeProcessor {
    private final Logger logger = LoggerFactory.getLogger(VerificationCodeProcessor.class);
    private final SubmitMessagesCache submitMessagesCache;
    private final VerificationCodePrinter verificationCodePrinter;
    private final NotificationMetrics notificationMetrics;

    public VerificationCodeProcessor(@Autowired SubmitMessagesCache submitMessagesCache, @Autowired VerificationCodePrinter verificationCodePrinter, @Autowired NotificationMetrics notificationMetrics) {
        this.submitMessagesCache = submitMessagesCache;
        this.verificationCodePrinter = verificationCodePrinter;
        this.notificationMetrics = notificationMetrics;
    }

    public void process(VerificationCode verificationCode) {
        if (submitMessagesCache.tryRegisterEvent(verificationCode)) {
            verificationCodePrinter.print(verificationCode);
            logger.debug("Redis cache dont have event with id={}, print message success", verificationCode.getEventId());
        } else {
            logger.warn("Kafka message exist in Redis cache with eventId={}", verificationCode.getEventId());
            notificationMetrics.incrementDuplicateCounter();
        }
    }
}
