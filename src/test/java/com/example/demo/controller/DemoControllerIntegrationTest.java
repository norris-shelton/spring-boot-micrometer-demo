package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.micrometer.tracing.Tracer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(DemoControllerIntegrationTest.class);
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private Tracer tracer;
    
    @Test
    public void testEndpointWithTracing() {
        // Log before request to show tracing context
        logger.info("Starting integration test with tracing");
        
        // Verify tracer is available
        assertNotNull(tracer, "Tracer should be available");
        
        // Make request to endpoint
        String response = restTemplate.getForObject(
                "http://localhost:" + port + "/api/process?input=integration-test", 
                String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint: {}", response);
        
        assertNotNull(response, "Response should not be null");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test with tracing");
    }
}
