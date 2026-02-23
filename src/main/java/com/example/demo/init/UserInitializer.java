package com.example.demo.init;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${User.username}")
    private String UserUsername;

    @Value("${User.password}")
    private String UserPassword;

    @Value("${User.email}")
    private String UserEmail;
    @Override
    public void run(String... args) throws Exception {
        try {
            String usernameLower = UserUsername.toLowerCase();
            String userEmailLower = UserEmail.toLowerCase();

            if (!userRepository.existsByEmail(userEmailLower)) {
                log.info("User '{}' not found. Creating user account...", userEmailLower);

                UserEntity userUser = UserEntity.builder()
                        .userId(UUID.randomUUID().toString())
                        .name(usernameLower)
                        .email(userEmailLower)
                        .password(passwordEncoder.encode(UserPassword))
                        .isAccountVerified(true)
                        .build();

                userRepository.save(userUser);
                log.info("User account created successfully with email: {}", userEmailLower);
            } else {
                log.info("User with email '{}' already exists. Skipping initialization.", userEmailLower);
            }
        } catch (Exception e) {
            log.error("Error initializing user account: {}", e.getMessage(), e);
            throw e;
        }
    }
}
