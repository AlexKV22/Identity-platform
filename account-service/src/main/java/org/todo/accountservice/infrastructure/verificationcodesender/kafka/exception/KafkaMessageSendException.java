package org.todo.accountservice.infrastructure.verificationcodesender.kafka.exception;

public class KafkaMessageSendException extends RuntimeException {

    public KafkaMessageSendException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
