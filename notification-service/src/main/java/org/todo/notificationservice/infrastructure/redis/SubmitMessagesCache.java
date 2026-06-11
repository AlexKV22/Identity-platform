package org.todo.notificationservice.infrastructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.todo.contracts.VerificationCode;
import org.todo.notificationservice.domain.dto.ListenVerificationCodeDto;

import java.time.Duration;

@Component
public class SubmitMessagesCache {
    private final RedisTemplate<String,String> redisTemplate;

    public SubmitMessagesCache(@Autowired RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryRegisterEvent(VerificationCode verificationCode) {
        return redisTemplate.opsForValue().setIfAbsent("verifyDuplicate:chalId:" + verificationCode.getEventId(),
                verificationCode.getEmail(), Duration.ofMinutes(4));
    }
}
