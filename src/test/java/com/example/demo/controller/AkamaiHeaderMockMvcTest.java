package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.micrometer.tracing.Tracer;

@SpringBootTest
@AutoConfigureMockMvc
public class AkamaiHeaderMockMvcTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private Tracer tracer;
    
    @Test
    public void testWithAkamaiHeader() throws Exception {
        String akamaiId = "abcdef1234567890abcdef1234567890";
        
        MvcResult result = mockMvc.perform(get("/api/process")
                .param("input", "mock-test")
                .header("akamaiGlobalIdentifier", akamaiId))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed: mock-test"))
                .andReturn();
        
        // The response should be successful and the trace context should be available
        // in the logs with the akamaiGlobalIdentifier value
    }
    
    @Test
    public void testWithoutAkamaiHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/process")
                .param("input", "standard-mock-test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Processed: standard-mock-test"))
                .andReturn();
        
        // The response should be successful and the trace context should be available
        // in the logs with a system-generated trace ID
    }
}
