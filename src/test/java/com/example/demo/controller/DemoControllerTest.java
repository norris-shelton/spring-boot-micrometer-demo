package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.WebConfig;
import com.example.demo.config.TracingConfig;
import com.example.demo.service.DemoService;

@WebMvcTest(value = DemoController.class, 
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
    classes = {WebConfig.class, TracingConfig.class}))
public class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemoService demoService;

    @Test
    public void testProcessRequest() throws Exception {
        // Arrange
        String testInput = "test-input";
        String expectedOutput = "Processed: " + testInput;
        
        when(demoService.processRequest(anyString())).thenReturn(expectedOutput);
        
        // Act & Assert
        mockMvc.perform(get("/api/process")
                .param("input", testInput))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutput));
    }
    
    @Test
    public void testProcessRequestWithDefaultValue() throws Exception {
        // Arrange
        String defaultInput = "default-input";
        String expectedOutput = "Processed: " + defaultInput;
        
        when(demoService.processRequest(defaultInput)).thenReturn(expectedOutput);
        
        // Act & Assert
        mockMvc.perform(get("/api/process"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutput));
    }
}
