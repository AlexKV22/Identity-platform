package org.todo.accountservice.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.todo.accountservice.infrastructure.dto.kafka.SendVerificationCodeDto;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface KafkaMapper {

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "code", source = "code")
    SendVerificationCodeDto toDto(UUID eventId, String email, Integer code);
}
