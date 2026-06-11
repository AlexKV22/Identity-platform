package org.todo.notificationservice.infrastructure.verificationcodelistener.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;

@Component
public class DltTopicListener {
    private final NotificationMetrics notificationMetrics;
    private static final Logger logger = LoggerFactory.getLogger("DLT_LOGGER");

    public DltTopicListener( @Autowired NotificationMetrics notificationMetrics) {
        this.notificationMetrics = notificationMetrics;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic}-dlt")
    public void listen(ConsumerRecord<?, ?> record) {
        logger.error(
                "DLT message. topic={}, partition={}, offset={}, value={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.value()
        );
        notificationMetrics.incrementDltCounter();
    }
}
