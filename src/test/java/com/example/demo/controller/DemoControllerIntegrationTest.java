package com.example.demo.controller;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(DemoControllerIntegrationTest.class);
    
    @Value("${local.server.port}")
    private int port;
    
    private RestClient restClient;
    
    @Autowired
    private Tracer tracer;
    
    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();
    }
    
    @Test
    public void testEndpointWithTracing() {
        // Log before request to show tracing context
        logger.info("Starting integration test with tracing");
        
        // Verify tracer is available
        assertNotNull(tracer, "Tracer should be available");
        
        // Make request to endpoint
        String response = restClient.get()
                .uri("/api/process?input=integration-test")
                .retrieve()
                .body(String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint: {}", response);
        
        assertNotNull(response, "Response should not be null");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test with tracing");
    }
}
