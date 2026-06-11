//package org.todo.notificationservice.unit;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import jakarta.validation.Validator;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.KafkaConsumer;
//import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
//import org.todo.notificationservice.service.VerificationCodeProcessor;
//
//import java.util.Collections;
//import java.util.Set;
//import java.util.UUID;
//
//
//@ExtendWith(MockitoExtension.class)
//class KafkaConsumerTest {
//    @Mock
//    private Validator validator;
//    @Mock
//    private VerificationCodeProcessor verificationCodeProcessor;
//
//    private ListenVerificationCodeDto listenVerificationCodeDto;
//
//    @InjectMocks
//    private KafkaConsumer kafkaConsumer;
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
//    void listenCorrectMessage() {
//        Mockito.when(validator.validate(listenVerificationCodeDto)).thenReturn(Collections.emptySet());
//        kafkaConsumer.listen(listenVerificationCodeDto, "verification-code-events-test-topic", 2, 4);
//        Mockito.verify(verificationCodeProcessor).process(listenVerificationCodeDto);
//        Mockito.verify(validator).validate(listenVerificationCodeDto);
//    }
//
//    @Test
//    void throwExceptionWhenFailedValidationMessage() {
//        ConstraintViolation<ListenVerificationCodeDto> validate = Mockito.mock(ConstraintViolation.class);
//        Mockito.when(validator.validate(listenVerificationCodeDto)).thenReturn(Set.of(validate));
//        Assertions.assertThrows(ConstraintViolationException.class, () -> kafkaConsumer.listen(listenVerificationCodeDto, "verification-code-events-test-topic", 2, 4));
//        Mockito.verify(validator).validate(listenVerificationCodeDto);
//        Mockito.verifyNoInteractions(verificationCodeProcessor);
//    }
//}
