package org.todo.accountservice.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.todo.accountservice.domain.codegenerator.VerificationCodeGenerator;
import org.todo.accountservice.domain.entity.User;
import org.todo.accountservice.infrastructure.dto.inputDto.AuthDtoRequest;
import org.todo.accountservice.infrastructure.dto.inputDto.VerificationCodeDtoRequest;
import org.todo.accountservice.infrastructure.dto.outputDto.AuthDtoResponse;
import org.todo.accountservice.infrastructure.redis.CodeCache;
import org.todo.accountservice.infrastructure.dto.redis.AuthDtoRedis;
import org.todo.accountservice.infrastructure.security.jwt.JWTTokenProvider;
import org.todo.accountservice.infrastructure.verificationcodesender.VerificationCodeSender;
import org.todo.accountservice.infrastructure.mapper.UserMapper;
import org.todo.accountservice.repository.UserRepository;


@Service
public class AuthServiceImpl implements AuthService {
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final UserMapper userMapper;
    private final VerificationCodeSender verificationCodeSender;
    private final UserRepository userRepository;
    private final JWTTokenProvider jwtTokenProvider;

    private final CodeCache codeCache;

    private final TransactionTemplate transactionTemplate;


    public AuthServiceImpl(@Autowired UserMapper userMapper,
                           @Autowired VerificationCodeSender verificationCodeSender,
                           @Autowired UserRepository userRepository,
                           @Autowired VerificationCodeGenerator verificationCodeGenerator,
                           @Autowired CodeCache codeCache,
                           @Autowired TransactionTemplate transactionTemplate,
                           @Autowired JWTTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.verificationCodeSender = verificationCodeSender;
        this.userRepository = userRepository;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.codeCache = codeCache;
        this.transactionTemplate = transactionTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public String initAuth(AuthDtoRequest authDtoRequest) {
        User user = userMapper.dtoToEntity(authDtoRequest);
        int generatedCode = verificationCodeGenerator.generateCode();
//        generatedCode = 1234;
        String uuid = codeCache.save(user, Integer.toString(generatedCode));
        verificationCodeSender.send(user.getEmail(), generatedCode);
        return uuid;
    }

    public AuthDtoResponse confirmAuth(VerificationCodeDtoRequest verificationCodeDtoRequest) {
        AuthDtoRedis authDtoRedis = codeCache.find(verificationCodeDtoRequest.UUID(), verificationCodeDtoRequest.code());
        User user = transactionTemplate.execute(status -> userRepository.findByEmail(authDtoRedis.email())
                .orElseGet(() -> userRepository.save(User.builder().email(authDtoRedis.email()).password(authDtoRedis.password()).build())));

        codeCache.delete(verificationCodeDtoRequest.UUID());
        String token = jwtTokenProvider.generateJWTToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        return userMapper.entityToDto(user, token, refreshToken);
    }
}
