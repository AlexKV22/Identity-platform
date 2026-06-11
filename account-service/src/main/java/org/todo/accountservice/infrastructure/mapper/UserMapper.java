package org.todo.accountservice.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.todo.accountservice.domain.entity.User;
import org.todo.accountservice.infrastructure.dto.inputDto.AuthDtoRequest;
import org.todo.accountservice.infrastructure.dto.outputDto.AuthDtoResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    AuthDtoResponse entityToDto(User user, String token, String refreshToken);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", source = "password")
    User dtoToEntity(AuthDtoRequest authDtoRequest);
}
