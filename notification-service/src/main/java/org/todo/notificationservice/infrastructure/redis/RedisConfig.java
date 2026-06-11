package org.todo.notificationservice.infrastructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;

@Configuration
public class RedisConfig {
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisConfig(@Autowired RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, ListenVerificationCodeDto> redisTemplate() {
        RedisTemplate<String, ListenVerificationCodeDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
