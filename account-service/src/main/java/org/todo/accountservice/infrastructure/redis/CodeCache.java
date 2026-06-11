package org.todo.accountservice.infrastructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.todo.accountservice.domain.entity.User;
import org.todo.accountservice.domain.exceptions.ExpiredCodeException;
import org.todo.accountservice.domain.exceptions.InvalidCodeException;
import org.todo.accountservice.infrastructure.dto.redis.AuthDtoRedis;

import java.time.Duration;
import java.util.UUID;

@Component
public class CodeCache {

    private final RedisTemplate<String, AuthDtoRedis> redisTemplate;

    public CodeCache(@Autowired RedisTemplate<String, AuthDtoRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String save(User user, String code) {
        UUID uuid = UUID.randomUUID();
        AuthDtoRedis dtoRedis = new AuthDtoRedis(user.getEmail(), user.getPassword(), code);
        redisTemplate.opsForValue().set("auth:chalId:" +  uuid, dtoRedis, Duration.ofMinutes(4));
        return uuid.toString();
    }

    public AuthDtoRedis find(String chalId, String code) {
        AuthDtoRedis dto = redisTemplate.opsForValue().get("auth:chalId:" + chalId);
        if (dto == null) {
            throw new ExpiredCodeException("Code is expired");
        } else if (!dto.code().equals(code)) {
            throw new InvalidCodeException("Wrong code");
        }
        return dto;
    }

    public void delete(String chalId) {
        redisTemplate.delete("auth:chalId:" + chalId);
    }
}
