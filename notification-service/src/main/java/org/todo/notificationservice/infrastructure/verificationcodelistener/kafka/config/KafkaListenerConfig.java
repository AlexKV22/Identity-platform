package org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.util.backoff.FixedBackOff;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;

@Configuration
public class KafkaListenerConfig {
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, VerificationCode> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2));
        defaultErrorHandler.addNotRetryableExceptions(ConstraintViolationException.class,
                DeserializationException.class,
                MethodArgumentNotValidException.class);
        return defaultErrorHandler;
    }
}
