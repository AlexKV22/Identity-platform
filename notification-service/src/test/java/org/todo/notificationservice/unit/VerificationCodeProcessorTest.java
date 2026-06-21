package org.todo.notificationservice.unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;
import org.todo.notificationservice.service.VerificationCodePrinter;
import org.todo.notificationservice.service.VerificationCodeProcessor;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class VerificationCodeProcessorTest {
    @Mock
    private SubmitMessagesCache submitMessagesCache;
    @Mock
    private VerificationCodePrinter verificationCodePrinter;
    @Mock
    private NotificationMetrics notificationMetrics;
    @Mock
    private Validator validator;

    private VerificationCode verificationCode;

    @InjectMocks
    VerificationCodeProcessor verificationCodeProcessor;

    @BeforeEach
    void set() {
        verificationCode = new VerificationCode(UUID.randomUUID(), "egor22", 1001);
    }

    @AfterEach
    void close() {
        verificationCode = null;
    }

    @Test
    void validateSuccessAndProcessSuccess() {
        Mockito.doReturn(Collections.emptySet()).when(validator).validate(any(ListenVerificationCodeDto.class));
        Mockito.when(submitMessagesCache.tryRegisterEvent(verificationCode)).thenReturn(true);
        verificationCodeProcessor.validateDto(verificationCode, "test-topic", 2L, 4, "test-group");
        Assertions.assertDoesNotThrow(() -> {});
        Mockito.verify(verificationCodePrinter).print(verificationCode);
        Mockito.verifyNoInteractions(notificationMetrics);
    }

    @Test
    void validateSuccessAndProcessFailed() {
        Mockito.doReturn(Collections.emptySet()).when(validator).validate(any(ListenVerificationCodeDto.class));
        Mockito.when(submitMessagesCache.tryRegisterEvent(verificationCode)).thenReturn(false);
        verificationCodeProcessor.validateDto(verificationCode, "test-topic", 2L, 4, "test-group");
        Assertions.assertDoesNotThrow(() -> {});
        Mockito.verifyNoInteractions(verificationCodePrinter);
        Mockito.verify(notificationMetrics).incrementDuplicateCounter();
    }

    @Test
    void throwExceptionWhenValidateFailed() {
        Mockito.doReturn(Set.of(Mockito.mock(ConstraintViolation.class))).when(validator).validate(any(ListenVerificationCodeDto.class));
        Assertions.assertThrows(ConstraintViolationException.class, () -> verificationCodeProcessor.validateDto(verificationCode, "test-topic", 2L, 4, "test-group"));
        Mockito.verifyNoInteractions(verificationCodePrinter);
        Mockito.verifyNoInteractions(notificationMetrics);
    }

    @Test
    void throwExceptionWhenRedisConnectionFailed() {
        Mockito.doReturn(Collections.emptySet()).when(validator).validate(any(ListenVerificationCodeDto.class));
        Mockito.doThrow(RedisConnectionFailureException.class).when(submitMessagesCache).tryRegisterEvent(verificationCode);
        Assertions.assertThrows(RedisConnectionFailureException.class, () -> verificationCodeProcessor.validateDto(verificationCode, "test-topic", 2L, 4, "test-group"));
        Mockito.verifyNoInteractions(verificationCodePrinter);
        Mockito.verifyNoInteractions(notificationMetrics);
    }
}
