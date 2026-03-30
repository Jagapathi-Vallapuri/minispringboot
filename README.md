# MiniSpringBoot

A learning project that combines:
- a regular Spring Boot application entrypoint, and
- a custom lightweight annotation-based web framework prototype (`@RestController` + `@GetMapping`) built on JDK `HttpServer`.

## Current Status

This repository is in an exploratory/prototype state.

- Spring Boot app context starts successfully in tests.
- The custom mini-framework components exist and are partially wired.
- The custom demo launcher in `src/main/java/com/mini/springboot/app/Main.java` is not currently a Java launcher entrypoint (details in Known Issues).

## Tech Stack

- Java 25
- Maven Wrapper (`./mvnw`)
- Spring Boot 4.0.5
- JUnit 5 (via `spring-boot-starter-test`)

## Project Structure

- `src/main/java/com/mini/springboot/MinispringbootApplication.java`
  - Standard Spring Boot entrypoint.
- `src/main/java/com/mini/springboot/app/HelloController.java`
  - Example controller for the custom framework.
- `src/main/java/com/mini/springboot/app/Main.java`
  - Intended custom framework launcher.
- `src/main/java/com/mini/springboot/framework/annotations`
  - Custom annotations (`@RestController`, `@GetMapping`).
- `src/main/java/com/mini/springboot/framework/context`
  - Classpath scanning and route/bean registration.
- `src/main/java/com/mini/springboot/framework/server/WebServer.java`
  - Embedded HTTP server and request dispatch.
- `src/test/java/com/mini/springboot/MinispringbootApplicationTests.java`
  - Spring Boot context load test.

## Prerequisites

- JDK 25 installed and available on `PATH`
- Linux/macOS: executable permission on wrapper script (`chmod +x mvnw` if needed)

## Build and Test

```bash
./mvnw clean test
```

Expected result:
- Tests pass and Spring context loads.

## Running the Spring Boot App

```bash
./mvnw spring-boot:run
```

This starts the standard Spring Boot application (`MinispringbootApplication`).

## Running the Custom Mini Framework

The custom framework server is intended to be launched from `com.mini.springboot.app.Main`, which creates:
- `ApplicationContext`
- scans `com.mini.springboot.app`
- starts `WebServer` on port `8080`

Current blocker:
- `Main.main()` is declared as `static void main()` (non-public, no `String[] args`), so it is not recognized as a Java entrypoint by the launcher.

To make it runnable, change it to:

```java
public static void main(String[] args)
```

Then run with a standard Java command or Maven exec plugin setup.

