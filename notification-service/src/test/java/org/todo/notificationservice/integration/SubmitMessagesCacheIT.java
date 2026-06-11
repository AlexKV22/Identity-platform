//package org.todo.notificationservice.integration;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
//import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;
//
//import java.util.UUID;
//
//
//@ActiveProfiles("test")
//@Testcontainers
//@SpringBootTest
//class SubmitMessagesCacheIT {
//
//    @Container
//    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2").withExposedPorts(6379);
//
//    @DynamicPropertySource
//    static void configure(DynamicPropertyRegistry dynamicPropertyRegistry) {
//        dynamicPropertyRegistry.add("spring.data.redis.host", redis::getHost);
//        dynamicPropertyRegistry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
//    }
//
//    @Autowired
//    private SubmitMessagesCache submitMessagesCache;
//
//    private ListenVerificationCodeDto listenVerificationCodeDto;
//
//    @BeforeEach
//    void set() {
//        listenVerificationCodeDto = new ListenVerificationCodeDto(UUID.randomUUID(), "egor22", 3412 );
//    }
//
//    @AfterEach
//    void close() {
//        listenVerificationCodeDto = null;
//    }
//
//    @Test
//    void tryRegisterEventWhenReturnTrue() {
//        boolean result = submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void tryRegisterEventWhenReturnFalse() {
//        submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto);
//        boolean result = submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto);
//        Assertions.assertFalse(result);
//    }
//}
