# Spring Boot 3 Micrometer Tracing Demo with Akamai Header Support

This project demonstrates Spring Micrometer tracing with Spring Boot 3 running on JDK 21. It includes a Spring MVC controller that calls a service, with SLF4J and Logback logging configured to show traceId and spanId values in log statements.

## Enhanced Feature
The project now supports setting the traceId from the `akamaiGlobalIdentifier` header if it's present in the request. This allows for consistent tracing across systems that use Akamai's global identifier.

## Project Structure

- Spring Boot 4.0.0-M1 with JDK 21
- Spring MVC controller and service
- Micrometer tracing with Brave implementation
- SLF4J and Logback for logging with traceId and spanId
- JUnit tests with MockMVC
- Custom interceptor for Akamai header processing

## Features

- REST endpoint that demonstrates tracing across controller and service
- Logback configuration that includes traceId and spanId in log output
- Comprehensive test suite including unit, integration, and MockMVC tests
- Support for setting traceId from akamaiGlobalIdentifier header

## Getting Started

### Prerequisites

- JDK 21
- Maven (or use the included Maven wrapper)

### Running the Application

```bash
./mvnw spring-boot:run
```

### Testing the Application

```bash
./mvnw test
```

## API Endpoints

- `GET /api/process?input=your-input` - Processes the input and returns a result

## Tracing and Logging

The application is configured to include traceId and spanId in all log statements. When you make a request to the API, you'll see log entries with the following pattern:

```
2023-05-27 12:34:56.789 INFO [b4565dc8c9c47dd4,e07ba7ba2714f4a8] --- [nio-8080-exec-1] c.e.demo.controller.DemoController      : Received request in controller with input: test
```

Where:
- `b4565dc8c9c47dd4` is the traceId
- `e07ba7ba2714f4a8` is the spanId

### Akamai Header Support

When a request includes the `akamaiGlobalIdentifier` header, the application will use that value as the traceId. This allows for consistent tracing across systems that use Akamai's global identifier.

Example request with Akamai header:

```
curl -H "akamaiGlobalIdentifier: abcdef1234567890abcdef1234567890" http://localhost:8080/api/process?input=test
```

The logs will show the Akamai identifier as the traceId:

```
2023-05-27 12:34:56.789 INFO [abcdef1234567890abcdef1234567890,e07ba7ba2714f4a8] --- [nio-8080-exec-1] c.e.demo.controller.DemoController      : Received request in controller with input: test and akamaiGlobalIdentifier: abcdef1234567890abcdef1234567890
```

## Implementation Details

The Akamai header support is implemented using a Spring MVC interceptor that extracts the header value and sets it as the traceId using Brave's API. The implementation:

1. Extracts the `akamaiGlobalIdentifier` header from the request
2. If present, normalizes the value to ensure it's a valid 32-character hex string
3. Creates a new trace context with the specified traceId
4. Makes this the active trace context for the request

## Testing with MockMVC

The project includes MockMVC tests that demonstrate how to test the controller and service integration, including tests with and without the Akamai header.
