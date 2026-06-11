package org.todo.accountservice.infrastructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.todo.accountservice.infrastructure.dto.redis.AuthDtoRedis;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    public RedisConfig (@Autowired RedisConnectionFactory redisConnectionFactory, @Autowired ObjectMapper objectMapper) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RedisTemplate<String, AuthDtoRedis> redisTemplate() {
         RedisTemplate<String, AuthDtoRedis> redisTemplate = new RedisTemplate<>();
         redisTemplate.setConnectionFactory(redisConnectionFactory);
         redisTemplate.setKeySerializer(new StringRedisSerializer());
         redisTemplate.setValueSerializer(new JacksonJsonRedisSerializer<>(objectMapper, AuthDtoRedis.class));
         return redisTemplate;
    }
}
