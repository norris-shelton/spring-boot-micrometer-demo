package com.example.demo.config;

import brave.Tracing;
import brave.propagation.TraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class AkamaiTraceIdInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiTraceIdInterceptor.class);
    private static final String AKAMAI_GLOBAL_IDENTIFIER = "akamaiGlobalIdentifier";

    @Autowired
    private Tracer tracer;
    
    @Autowired
    private Tracing braveTracing;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String akamaiId = request.getHeader(AKAMAI_GLOBAL_IDENTIFIER);
        
        if (akamaiId != null && !akamaiId.isEmpty()) {
            logger.debug("Found akamaiGlobalIdentifier header: {}", akamaiId);
            
            try {
                // Use Brave's direct API to create a trace context with the specified traceId
                // This requires a valid 16-byte (128-bit) hex string
                String normalizedId = normalizeTraceId(akamaiId);
                
                // Parse the hex string to get the high and low bits for the trace ID
                long traceIdHigh = Long.parseUnsignedLong(normalizedId.substring(0, 16), 16);
                long traceIdLow = Long.parseUnsignedLong(normalizedId.substring(16), 16);
                
                // Create a new trace context with the specified trace ID
                TraceContext context = TraceContext.newBuilder()
                    .traceIdHigh(traceIdHigh)
                    .traceId(traceIdLow)
                    .spanId(1L)  // Use fixed span ID since nextSpanId() is not available
                    .build();
                
                // Create a new span with this context
                brave.Span braveSpan = braveTracing.tracer().toSpan(context).name("akamai-trace");
                braveSpan.start();
                
                // Store the brave span for later cleanup
                request.setAttribute("brave-akamai-span", braveSpan);
                
                logger.info("Set traceId from akamaiGlobalIdentifier: {}", akamaiId);
            } catch (Exception e) {
                logger.warn("Failed to set traceId from akamaiGlobalIdentifier: {}", akamaiId, e);
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        brave.Span braveSpan = (brave.Span) request.getAttribute("brave-akamai-span");
        if (braveSpan != null) {
            braveSpan.finish();
            logger.debug("Ended akamai span");
        }
    }
    
    /**
     * Normalize the trace ID to ensure it's a valid 32-character hex string.
     * If shorter, pad with zeros. If longer, truncate.
     */
    private String normalizeTraceId(String id) {
        // Remove any non-hex characters
        String hexOnly = id.replaceAll("[^0-9a-fA-F]", "");
        
        // Ensure it's 32 characters (16 bytes)
        if (hexOnly.length() < 32) {
            // Pad with leading zeros
            return "0".repeat(32 - hexOnly.length()) + hexOnly;
        } else if (hexOnly.length() > 32) {
            // Truncate to 32 characters
            return hexOnly.substring(0, 32);
        }
        
        return hexOnly;
    }
}
