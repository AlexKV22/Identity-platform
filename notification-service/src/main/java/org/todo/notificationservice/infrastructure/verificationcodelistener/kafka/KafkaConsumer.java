package org.todo.notificationservice.infrastructure.verificationcodelistener.kafka;

import io.micrometer.core.instrument.Timer;
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
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.service.VerificationCodeProcessor;

@Component
@Validated
public class KafkaConsumer {
    private final String groupId;
    private final VerificationCodeProcessor verificationCodeProcessor;
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final NotificationMetrics notificationMetrics;

    public KafkaConsumer(@Autowired VerificationCodeProcessor verificationCodeProcessor,
                         @Autowired NotificationMetrics notificationMetrics,
                         @Value("${spring.kafka.consumer.group-id}") String groupId) {
        this.verificationCodeProcessor = verificationCodeProcessor;
        this.notificationMetrics = notificationMetrics;
        this.groupId = groupId;
    }

    //Есть нюанс - если дессериализация авро прошла успешно, но ошибка далее - в длт полетит авро,
    // а если до дессериализации не дошло вообще - полетят байты,
    //поэтому нужно настроить настрйоки продюсера(сам кафка темплейт)
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(VerificationCode verificationCode, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) long offset,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        Timer.Sample sample = notificationMetrics.startProcessing();
        logger.debug("Received Kafka message from topic={} with groupId={}, offset={}, partition={}",
                topic, groupId, offset, partition );
        verificationCodeProcessor.validateDto(verificationCode, topic, offset, partition, groupId);
        logger.debug("Kafka message processed finish from topic={} with groupId={}, offset={}, partition={}",
                topic, groupId, offset, partition );
        notificationMetrics.stopProcessing(sample);
    }
}
