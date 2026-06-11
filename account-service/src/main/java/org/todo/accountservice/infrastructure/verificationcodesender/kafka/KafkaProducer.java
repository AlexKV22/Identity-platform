package org.todo.accountservice.infrastructure.verificationcodesender.kafka;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.todo.accountservice.infrastructure.dto.kafka.SendVerificationCodeDto;
import org.todo.accountservice.infrastructure.verificationcodesender.kafka.exception.KafkaMessageSendException;
import org.todo.contracts.VerificationCode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, VerificationCode> kafkaTemplate;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    public KafkaProducer(@Autowired  KafkaTemplate<String, VerificationCode> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "sendVerificationCodeFallback")
    @Bulkhead(name = "kafkaProducer", fallbackMethod = "sendVerificationCodeFallback")
    public void send(VerificationCode verificationCode) {
        try {
            kafkaTemplate.send(topic, verificationCode.getEmail(), verificationCode).get(6, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaMessageSendException("Interrupted when sending message", e);
        } catch(ExecutionException e) {
            throw new KafkaMessageSendException("Failed to send verification code to kafka", e);
        } catch (TimeoutException e) {
            throw new KafkaMessageSendException("Timeout for send verification code to kafka", e);
        }
    }

    private void sendVerificationCodeFallback(VerificationCode verificationCode, Throwable exception) {
        if (exception instanceof KafkaMessageSendException kafkaMessageSendException) {
            throw kafkaMessageSendException;
        } else if (exception instanceof CallNotPermittedException) {
            throw new KafkaMessageSendException("Kafka producer circuit breaker is open", exception);
        } else if (exception instanceof BulkheadFullException) {
            throw new KafkaMessageSendException("Too many concurrent send verification code requests", exception);
        }
        throw new KafkaMessageSendException("Failed send verification code", exception);
    }
}
