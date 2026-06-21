package org.todo.notificationservice.unit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.todo.notificationservice.infrastructure.metrics.NotificationMetrics;
import org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.DltTopicListener;

@ExtendWith(MockitoExtension.class)
class DltTopicListenerTest {

    @Mock
    private NotificationMetrics notificationMetrics;

    @InjectMocks
    private DltTopicListener dltTopicListener;

    @Test
    void listenTest() {
        Mockito.doNothing().when(notificationMetrics).incrementDltCounter();
        ConsumerRecord<?, ?> record = new ConsumerRecord<>("test-topic", 0, 0, "test-key", "test-value");
        dltTopicListener.listen(record);
        Mockito.verify(notificationMetrics).incrementDltCounter();
    }
}
