package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoServiceTest {

    @Autowired
    private DemoService demoService;
    
    @Test
    public void testProcessRequest() {
        // Arrange
        String input = "test-input";
        
        // Act
        String result = demoService.processRequest(input);
        
        // Assert
        assertNotNull(result);
        assertEquals("Processed: " + input, result);
    }
}
