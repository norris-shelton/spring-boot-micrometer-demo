package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.DemoService;

@RestController
@RequestMapping("/api")
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    
    private final DemoService demoService;
    
    @Autowired
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }
    
    @GetMapping("/process")
    public String processRequest(
            @RequestParam(defaultValue = "default-input") String input,
            @RequestHeader(value = "akamaiGlobalIdentifier", required = false) String akamaiId) {
        
        if (akamaiId != null && !akamaiId.isEmpty()) {
            logger.info("Received request in controller with input: {} and akamaiGlobalIdentifier: {}", input, akamaiId);
        } else {
            logger.info("Received request in controller with input: {}", input);
        }
        
        // Call the service method which will be traced
        String result = demoService.processRequest(input);
        
        logger.info("Controller completed processing with result: {}", result);
        return result;
    }
}
