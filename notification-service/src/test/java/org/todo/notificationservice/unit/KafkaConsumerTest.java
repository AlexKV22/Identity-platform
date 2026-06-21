package org.todo.notificationservice.unit;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.KafkaConsumer;
import org.todo.notificationservice.service.VerificationCodeProcessor;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {
    @Mock
    private VerificationCodeProcessor verificationCodeProcessor;
    @Mock
    private NotificationMetrics notificationMetrics;

    private VerificationCode verificationCode;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void set() {
        verificationCode = new VerificationCode(UUID.randomUUID(), "egor22", 1001);
        kafkaConsumer = new KafkaConsumer(verificationCodeProcessor, notificationMetrics, "group-id-test");
    }

    @AfterEach
    void close() {
        verificationCode = null;
        kafkaConsumer = null;
    }

    @Test
    void listenCorrectMessage() {
        Timer.Sample mock = Mockito.mock(Timer.Sample.class);
        Mockito.when(notificationMetrics.startProcessing()).thenReturn(mock);
        Mockito.doNothing().when(notificationMetrics).stopProcessing(mock);
        Mockito.doNothing().when(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2L, 4, "group-id-test");
        kafkaConsumer.listen(verificationCode, "verification-code-events-test-topic", 2L, 4);
        Mockito.verify(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2L, 4, "group-id-test");
        Mockito.verify(notificationMetrics).startProcessing();
        Mockito.verify(notificationMetrics).stopProcessing(mock);
    }

    @Test
    void throwExceptionWhenFailedValidationMessage() {
        Timer.Sample mock = Mockito.mock(Timer.Sample.class);
        Mockito.when(notificationMetrics.startProcessing()).thenReturn(mock);
        Mockito.doThrow(ConstraintViolationException.class).when(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2, 4, "group-id-test");
        Assertions.assertThrows(ConstraintViolationException.class, () -> kafkaConsumer.listen(verificationCode, "verification-code-events-test-topic", 2, 4));
        Mockito.verify(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2, 4, "group-id-test");
        Mockito.verify(notificationMetrics).startProcessing();
    }

    @Test
    void throwExceptionWhenFailedRedisConnection() {
        Timer.Sample mock = Mockito.mock(Timer.Sample.class);
        Mockito.when(notificationMetrics.startProcessing()).thenReturn(mock);
        Mockito.doThrow(RedisConnectionFailureException.class).when(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2, 4, "group-id-test");
        Assertions.assertThrows(RedisConnectionFailureException.class, () -> kafkaConsumer.listen(verificationCode, "verification-code-events-test-topic", 2, 4));
        Mockito.verify(verificationCodeProcessor).validateDto(verificationCode, "verification-code-events-test-topic", 2, 4, "group-id-test");
        Mockito.verify(notificationMetrics).startProcessing();
    }
}
