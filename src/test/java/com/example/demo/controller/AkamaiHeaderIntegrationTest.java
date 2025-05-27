package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import io.micrometer.tracing.Tracer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AkamaiHeaderIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiHeaderIntegrationTest.class);
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private Tracer tracer;
    
    @Test
    public void testEndpointWithAkamaiHeader() {
        // Create custom headers with Akamai identifier
        String akamaiId = "1234567890abcdef1234567890abcdef";
        HttpHeaders headers = new HttpHeaders();
        headers.set("akamaiGlobalIdentifier", akamaiId);
        
        // Log before request to show tracing context
        logger.info("Starting integration test with Akamai header");
        
        // Verify tracer is available
        assertNotNull(tracer, "Tracer should be available");
        
        // Make request to endpoint with custom headers
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/process?input=akamai-test", 
                HttpMethod.GET, entity, String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint with Akamai header: {}", response.getBody());
        
        assertNotNull(response.getBody(), "Response should not be null");
        assertEquals("Processed: akamai-test", response.getBody(), "Response content should match expected");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test with Akamai header");
    }
    
    @Test
    public void testEndpointWithoutAkamaiHeader() {
        // Log before request to show tracing context
        logger.info("Starting integration test without Akamai header");
        
        // Make request to endpoint without custom headers
        String response = restTemplate.getForObject(
                "http://localhost:" + port + "/api/process?input=standard-test", 
                String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint without Akamai header: {}", response);
        
        assertNotNull(response, "Response should not be null");
        assertEquals("Processed: standard-test", response, "Response content should match expected");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test without Akamai header");
    }
}
