package org.todo.notificationservice.infrastructure.verificationcodelistener.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import jakarta.validation.ConstraintViolationException;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.util.backoff.FixedBackOff;
import org.todo.contracts.VerificationCode;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaListenerConfig {
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2));
        defaultErrorHandler.addNotRetryableExceptions(ConstraintViolationException.class,
                DeserializationException.class,
                MethodArgumentNotValidException.class);
        return defaultErrorHandler;
    }

    @Bean
    public ProducerFactory<Object, Object> dltProducerFactory(KafkaProperties kafkaProperties) {
        Map<Class<?>, Serializer<?>> keySerializers = new HashMap<>();
        keySerializers.put(String.class, new StringSerializer());
        keySerializers.put(byte[].class, new ByteArraySerializer());
        DelegatingByTypeSerializer keySerializer = new DelegatingByTypeSerializer(keySerializers);

        Map<Class<?>, Serializer<?>> valueSerializers = new HashMap<>();
        valueSerializers.put(VerificationCode.class, new KafkaAvroSerializer());
        valueSerializers.put(byte[].class, new ByteArraySerializer());
        DelegatingByTypeSerializer valueSerializer = new DelegatingByTypeSerializer(valueSerializers);
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(),  keySerializer, valueSerializer);
    }

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate(ProducerFactory<Object, Object> dltProducerFactory) {
        return new KafkaTemplate<>(dltProducerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], byte[]> dltKafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> map = new HashMap<>(kafkaProperties.buildConsumerProperties());
        DefaultKafkaConsumerFactory<byte[], byte[]> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<byte[], byte[]>(map, new ByteArrayDeserializer(), new ByteArrayDeserializer());
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(defaultKafkaConsumerFactory);
        return concurrentKafkaListenerContainerFactory;
    }
}
