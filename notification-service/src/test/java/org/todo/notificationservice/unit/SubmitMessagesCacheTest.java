//package org.todo.notificationservice.unit;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
//import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;
//
//import java.time.Duration;
//import java.util.UUID;
//
//
//@ExtendWith(MockitoExtension.class)
//class SubmitMessagesCacheTest {
//    @Mock
//    private RedisTemplate<String,String> redisTemplate;
//
//    @InjectMocks
//    private SubmitMessagesCache submitMessagesCache;
//
//    private ListenVerificationCodeDto listenVerificationCodeDto;
//
//
//    @BeforeEach
//    void set() {
//        listenVerificationCodeDto = new ListenVerificationCodeDto(UUID.randomUUID(), "egor22", 1001);
//    }
//
//    @AfterEach
//    void close() {
//        listenVerificationCodeDto = null;
//    }
//
//    @Test
//    void tryRegisterEventWhenReturnTrue() {
//        ValueOperations mock = Mockito.mock(ValueOperations.class);
//        Mockito.when(redisTemplate.opsForValue()).thenReturn(mock);
//        Mockito.when(redisTemplate.opsForValue().setIfAbsent("verifyDuplicate:chalId:" + listenVerificationCodeDto.eventId(),
//                listenVerificationCodeDto.email(), Duration.ofMinutes(4))).thenReturn(true);
//        Assertions.assertDoesNotThrow(() -> submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto));
//    }
//
//    @Test
//    void tryRegisterEventWhenReturnFalse() {
//        ValueOperations mock = Mockito.mock(ValueOperations.class);
//        Mockito.when(redisTemplate.opsForValue()).thenReturn(mock);
//        Mockito.when(redisTemplate.opsForValue().setIfAbsent("verifyDuplicate:chalId:" + listenVerificationCodeDto.eventId(),
//                listenVerificationCodeDto.email(), Duration.ofMinutes(4))).thenReturn(false);
//        Assertions.assertDoesNotThrow(() -> submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto));
//    }
//}
