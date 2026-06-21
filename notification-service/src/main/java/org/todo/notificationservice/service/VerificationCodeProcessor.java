package org.todo.notificationservice.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;

import java.util.Set;

@Service
public class VerificationCodeProcessor {
    private final Logger logger = LoggerFactory.getLogger(VerificationCodeProcessor.class);
    private final SubmitMessagesCache submitMessagesCache;
    private final VerificationCodePrinter verificationCodePrinter;
    private final NotificationMetrics notificationMetrics;

    private final Validator validator;

    public VerificationCodeProcessor(@Autowired SubmitMessagesCache submitMessagesCache,
                                     @Autowired VerificationCodePrinter verificationCodePrinter,
                                     @Autowired NotificationMetrics notificationMetrics,
                                     @Autowired Validator validator) {
        this.submitMessagesCache = submitMessagesCache;
        this.verificationCodePrinter = verificationCodePrinter;
        this.notificationMetrics = notificationMetrics;
        this.validator = validator;
    }

    private void process(VerificationCode verificationCode) {
        if (Boolean.TRUE.equals(submitMessagesCache.tryRegisterEvent(verificationCode))) {
            verificationCodePrinter.print(verificationCode);
            logger.debug("Redis cache dont have event with id={}, print message success", verificationCode.getEventId());
        } else {
            logger.warn("Kafka message exist in Redis cache with eventId={}", verificationCode.getEventId());
            notificationMetrics.incrementDuplicateCounter();
        }
    }

    public void validateDto(VerificationCode verificationCode, String topic, long offset, int partition, String groupId) {
        ListenVerificationCodeDto dto = new ListenVerificationCodeDto(verificationCode.getEventId(), verificationCode.getEmail(), verificationCode.getCode());
        Set<ConstraintViolation<@Valid ListenVerificationCodeDto>> validate = validator.validate(dto);
        if(!validate.isEmpty()) {
            logger.error("Failed validation Kafka message from topic={} with groupId={}, offset={}, partition={}",
                    topic, groupId, offset, partition );
            throw new ConstraintViolationException(validate);
            //TODO(alex): modify validation to avro with annotation
        } else {
            process(verificationCode);
        }
    }
}
