//package org.todo.notificationservice.integration;
//
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.kafka.KafkaContainer;
//import org.awaitility.Awaitility;
//import org.testcontainers.utility.DockerImageName;
//import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
//import org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.DltTopicListener;
//import org.todo.notificationservice.service.VerificationCodeProcessor;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ExecutionException;
//
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
//@Testcontainers
//@SpringBootTest
//class KafkaConsumerIT {
//
//    @Value("${spring.kafka.consumer.topic}")
//    private String topic;
//
//    @Container
//    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
//
//    @DynamicPropertySource
//    static void configure(DynamicPropertyRegistry dynamicPropertyRegistry) {
//        dynamicPropertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
//    }
//
//    @Autowired
//    private KafkaTemplate<String, ListenVerificationCodeDto> kafkaTemplate;
//
//    @Autowired
//    private ConsumerFactory<String, ListenVerificationCodeDto> consumerFactory;
//
//    @MockitoBean
//    private DltTopicListener dltTopicListener;
//
//    @MockitoBean
//    private VerificationCodeProcessor verificationCodeProcessor;
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
//    void consumeMessage() throws ExecutionException, InterruptedException {
//        kafkaTemplate.send(topic, "key", listenVerificationCodeDto).get();
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10)).untilAsserted(() -> verify(verificationCodeProcessor).process(listenVerificationCodeDto));
//    }
//
//    @Test
//    void failConsumeWhenNullMessage() throws ExecutionException, InterruptedException {
//        kafkaTemplate.send(topic, "key", null).get();
//        try (Consumer<String, ListenVerificationCodeDto> dltConsumer = consumerFactory.createConsumer("dlt-test-group", "dlt-test-client")) {
//            dltConsumer.subscribe(List.of(topic + "-dlt"));
//            ConsumerRecord<String, ListenVerificationCodeDto> record = KafkaTestUtils.getSingleRecord(dltConsumer, topic + "-dlt", Duration.ofSeconds(10));
//            Assertions.assertThat(record.key()).isEqualTo("key");
//            Assertions.assertThat(record.value()).isNull();
//        }
//        Awaitility.await().during(Duration.ofSeconds(2))
//                .atMost(Duration.ofSeconds(10)).untilAsserted(() -> verifyNoInteractions(verificationCodeProcessor));
//
//    }
//
//    @Test
//    void failConsumeMessageWhenThrowExceptionFromMethod() throws ExecutionException, InterruptedException {
//        doThrow(new RuntimeException("print failed")).when(verificationCodeProcessor).process(listenVerificationCodeDto);
//        kafkaTemplate.send(topic, "key", listenVerificationCodeDto).get();
//        try (Consumer<String, ListenVerificationCodeDto> dltConsumer = consumerFactory.createConsumer("dlt-test-group", "dlt-test-client")) {
//            dltConsumer.subscribe(List.of(topic + "-dlt"));
//            ConsumerRecord<String, ListenVerificationCodeDto> record = KafkaTestUtils.getSingleRecord(dltConsumer, topic + "-dlt", Duration.ofSeconds(10));
//            Assertions.assertThat(record.key()).isEqualTo("key");
//            Assertions.assertThat(record.value()).isEqualTo(listenVerificationCodeDto);
//        }
//        Awaitility.await().during(Duration.ofSeconds(2))
//                .atMost(Duration.ofSeconds(10)).untilAsserted(() -> verify(verificationCodeProcessor, times(3)).process(listenVerificationCodeDto));
//    }
//
//    @Test
//    void failConsumeMessageWhenValidationFieldsError() throws ExecutionException, InterruptedException {
//        ListenVerificationCodeDto invalidDto = new ListenVerificationCodeDto(UUID.randomUUID(), "", 500);
//        kafkaTemplate.send(topic, "key", invalidDto).get();
//        try (Consumer<String, ListenVerificationCodeDto> dltConsumer = consumerFactory.createConsumer("dlt-test-group", "dlt-test-client")) {
//            dltConsumer.subscribe(List.of(topic + "-dlt"));
//            ConsumerRecord<String, ListenVerificationCodeDto> record = KafkaTestUtils.getSingleRecord(dltConsumer, topic + "-dlt", Duration.ofSeconds(10));
//            Assertions.assertThat(record.key()).isEqualTo("key");
//            Assertions.assertThat(record.value()).isEqualTo(invalidDto);
//        }
//        Awaitility.await().during(Duration.ofSeconds(2))
//                .atMost(Duration.ofSeconds(10)).untilAsserted(() -> verifyNoInteractions(verificationCodeProcessor));
//    }
//}
