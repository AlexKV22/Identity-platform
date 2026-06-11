package org.todo.notificationservice.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {
    private final Counter processedCounter;
    private final Counter duplicateCounter;
    private final Counter dltCounter;
    private final Timer processingTimer;

    private final MeterRegistry meterRegistry;

    public NotificationMetrics(@Autowired MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.processedCounter = Counter.builder("notification_processed").description("Successfully processed notifications").register(meterRegistry);
        this.duplicateCounter = Counter.builder("notification_duplicates").description("Duplicate notifications").register(meterRegistry);
        this.dltCounter = Counter.builder("notification_dlt").description("Messages sent to DLT").register(meterRegistry);
        this.processingTimer = Timer.builder("notification_processing").publishPercentileHistogram().publishPercentiles(0.5, 0.95, 0.99).register(meterRegistry);
    }

    public void incrementProcessedCounter() {
        processedCounter.increment();
    }
    public void incrementDuplicateCounter() {
        duplicateCounter.increment();
    }
    public void incrementDltCounter() {
        dltCounter.increment();
    }

    public Timer.Sample startProcessing() {
        return Timer.start(meterRegistry);
    }

    public void stopProcessing(Timer.Sample sample) {
        sample.stop(Timer.builder("notification.processing.duration")
                .description("Notification processing time")
                .register(meterRegistry));
    }
}
