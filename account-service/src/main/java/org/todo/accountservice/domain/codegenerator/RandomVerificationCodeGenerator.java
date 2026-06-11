package org.todo.accountservice.domain.codegenerator;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomVerificationCodeGenerator implements VerificationCodeGenerator {
    private final Random random = new Random();

    @Override
    public int generateCode() {
        return random.nextInt(1000, 5001);
    }
}
