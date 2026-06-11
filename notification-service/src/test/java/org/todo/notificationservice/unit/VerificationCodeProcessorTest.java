//package org.todo.notificationservice.unit;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
//import org.todo.notificationservice.infrastructure.redis.SubmitMessagesCache;
//import org.todo.notificationservice.service.VerificationCodePrinter;
//import org.todo.notificationservice.service.VerificationCodeProcessor;
//
//import java.util.UUID;
//
//@ExtendWith(MockitoExtension.class)
//class VerificationCodeProcessorTest {
//    @Mock
//    private SubmitMessagesCache submitMessagesCache;
//    @Mock
//    private VerificationCodePrinter verificationCodePrinter;
//
//    private ListenVerificationCodeDto listenVerificationCodeDto;
//
//    @InjectMocks
//    VerificationCodeProcessor verificationCodeProcessor;
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
//    void validVerificationCodeProcess() {
//        Mockito.when(submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto)).thenReturn(true);
//        verificationCodeProcessor.process(listenVerificationCodeDto);
//        Mockito.verify(verificationCodePrinter).print(listenVerificationCodeDto);
//    }
//
//    @Test
//    void invalidVerificationCodeProcess() {
//        Mockito.when(submitMessagesCache.tryRegisterEvent(listenVerificationCodeDto)).thenReturn(false);
//        verificationCodeProcessor.process(listenVerificationCodeDto);
//        Mockito.verifyNoInteractions(verificationCodePrinter);
//    }
//}
