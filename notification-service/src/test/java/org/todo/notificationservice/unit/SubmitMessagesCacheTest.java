package org.todo.notificationservice.unit;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;

import java.time.Duration;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class SubmitMessagesCacheTest {
    @Mock
    private RedisTemplate<String,String> redisTemplate;

    @InjectMocks
    private SubmitMessagesCache submitMessagesCache;

    private VerificationCode verificationCode;


    @BeforeEach
    void set() {
        verificationCode = new VerificationCode(UUID.randomUUID(), "egor22", 1001);
    }

    @AfterEach
    void close() {
        verificationCode = null;
    }

    @Test
    void tryRegisterEventWhenReturnTrue() {
        ValueOperations mock = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mock);
        Mockito.when(mock.setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(),
                verificationCode.getEmail(), Duration.ofMinutes(4))).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> submitMessagesCache.tryRegisterEvent(verificationCode));
        Mockito.verify(mock).setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(),
                verificationCode.getEmail(), Duration.ofMinutes(4));
    }

    @Test
    void tryRegisterEventWhenReturnFalse() {
        ValueOperations mock = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mock);
        Mockito.when(mock.setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(),
                verificationCode.getEmail(), Duration.ofMinutes(4))).thenReturn(false);
        Assertions.assertDoesNotThrow(() -> submitMessagesCache.tryRegisterEvent(verificationCode));
        Mockito.verify(mock).setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(),
                verificationCode.getEmail(), Duration.ofMinutes(4));
    }

    @Test
    void tryRegisterEventWhenThrowException() {
        ValueOperations mock = Mockito.mock(ValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(mock);
        Mockito.doThrow(RedisConnectionFailureException.class).when(mock).setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(), verificationCode.getEmail(), Duration.ofMinutes(4));
        Assertions.assertThrows(RedisConnectionFailureException.class, () -> submitMessagesCache.tryRegisterEvent(verificationCode));

    }
}
