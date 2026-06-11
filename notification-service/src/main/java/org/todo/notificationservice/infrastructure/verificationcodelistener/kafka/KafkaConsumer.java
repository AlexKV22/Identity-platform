package org.todo.notificationservice.infrastructure.verificationcodelistener.kafka;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.service.VerificationCodeProcessor;

import java.util.Set;

@Component
@Validated
public class KafkaConsumer {
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private final VerificationCodeProcessor verificationCodeProcessor;
    private final Validator validator;
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final NotificationMetrics notificationMetrics;

    public KafkaConsumer(@Autowired VerificationCodeProcessor verificationCodeProcessor, @Autowired Validator validator, @Autowired NotificationMetrics notificationMetrics) {
        this.verificationCodeProcessor = verificationCodeProcessor;
        this.validator = validator;
        this.notificationMetrics = notificationMetrics;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(VerificationCode verificationCode, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.err.println(verificationCode.getEventId().getClass());
        Timer.Sample sample = notificationMetrics.startProcessing();
        logger.debug("Received Kafka message from topic={} with groupId={}, offset={}, partition={}",
                topic, groupId, offset, partition );
//        Set<ConstraintViolation<@Valid VerificationCode>> validate = validator.validate(verificationCode);
//        if(!validate.isEmpty()) {
//            logger.error("Failed validation Kafka message from topic={} with groupId={}, offset={}, partition={}",
//                    topic, groupId, offset, partition );
//            throw new ConstraintViolationException(validate);
//        }
        verificationCodeProcessor.process(verificationCode);
        logger.debug("Kafka message processed finish from topic={} with groupId={}, offset={}, partition={}",
                topic, groupId, offset, partition );
        notificationMetrics.stopProcessing(sample);
    }
}
