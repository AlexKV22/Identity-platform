package org.todo.accountservice.infrastructure.verificationcodesender.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.todo.accountservice.infrastructure.dto.kafka.SendVerificationCodeDto;
import org.todo.accountservice.infrastructure.mapper.KafkaMapper;
import org.todo.accountservice.infrastructure.verificationcodesender.VerificationCodeSender;
import org.todo.contracts.VerificationCode;

import java.util.UUID;

@Component
public class KafkaVerificationCodeSender implements VerificationCodeSender {
    private final KafkaMapper kafkaMapper;
    private final KafkaProducer kafkaProducer;

    public KafkaVerificationCodeSender(@Autowired KafkaProducer kafkaProducer, @Autowired KafkaMapper kafkaMapper) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaMapper = kafkaMapper;
    }

    public void send(String email, Integer code) {
        UUID uuid = UUID.randomUUID();
//        uuid = UUID.fromString("e6495267-97ce-4b0a-8462-b68cc9fb1f39");
        SendVerificationCodeDto dto = kafkaMapper.toDto(uuid, email, code);
        VerificationCode verificationCode = VerificationCode.newBuilder().setEventId(uuid).setEmail(email).setCode(code).build();
        kafkaProducer.send(verificationCode);
    }
}
