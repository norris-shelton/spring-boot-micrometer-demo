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
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AkamaiHeaderIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiHeaderIntegrationTest.class);
    
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
        String response = restClient.get()
                .uri("/api/process?input=akamai-test")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .body(String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint with Akamai header: {}", response);
        
        assertNotNull(response, "Response should not be null");
        assertEquals("Processed: akamai-test", response, "Response content should match expected");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test with Akamai header");
    }
    
    @Test
    public void testEndpointWithoutAkamaiHeader() {
        // Log before request to show tracing context
        logger.info("Starting integration test without Akamai header");
        
        // Make request to endpoint without custom headers
        String response = restClient.get()
                .uri("/api/process?input=standard-test")
                .retrieve()
                .body(String.class);
        
        // Log after request to show tracing context is propagated
        logger.info("Received response from endpoint without Akamai header: {}", response);
        
        assertNotNull(response, "Response should not be null");
        assertEquals("Processed: standard-test", response, "Response content should match expected");
        
        // Log to demonstrate trace context is maintained
        logger.info("Completed integration test without Akamai header");
    }
}
