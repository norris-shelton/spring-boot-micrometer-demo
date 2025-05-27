package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);
    
    public String processRequest(String input) {
        logger.info("Processing request in service with input: {}", input);
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String result = "Processed: " + input;
        logger.info("Request processing completed with result: {}", result);
        
        return result;
    }
}
