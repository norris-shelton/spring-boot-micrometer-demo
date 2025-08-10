package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.Tracing;

@Configuration
public class TracingConfig {

    /**
     * This bean is needed to access the Brave Tracing instance directly
     * for setting custom trace IDs from the akamaiGlobalIdentifier header.
     */
    @Bean
    public Tracing braveTracing() {
        // Create a simple Brave Tracing instance
        return Tracing.newBuilder()
                .localServiceName("spring-boot-micrometer-demo")
                .build();
    }
}
