package com.example.mvc_with_jpa_1.config;

import com.example.mvc_with_jpa_1.repository.CommentEmRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CommentEmRepositoryTestConfig {

    @Bean
    public CommentEmRepository commentEmRepository(EntityManager em) {
        return new CommentEmRepository(em);
    }
}
