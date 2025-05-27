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
    public Tracing braveTracing(io.micrometer.tracing.Tracer tracer) {
        // Access the underlying Brave implementation
        if (tracer instanceof io.micrometer.tracing.brave.bridge.BraveTracer braveTracer) {
            return braveTracer.unwrap().tracing();
        }
        throw new IllegalStateException("Expected BraveTracer implementation but found: " + tracer.getClass().getName());
    }
}
