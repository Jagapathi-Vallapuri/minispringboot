# MiniSpringBoot

MiniSpringBoot is a learning project with two runtime tracks:

1. A regular Spring Boot application entrypoint.
2. A custom lightweight HTTP framework built on JDK `HttpServer`.

## Current Status

The project is in prototype mode and actively evolving.

- Spring Boot test context loads successfully.
- Custom framework currently supports:
  - component scanning for `@RestController` and `@Service`
  - field injection via `@Autowired`
  - route registration with `@GetMapping` and `@PostMapping`
  - verb-aware route lookup (`path + HTTP method`)
  - path variable, query param, and request body argument binding
  - simple JSON body parsing into `Map`
- Custom framework launcher still needs a valid Java entrypoint signature.

## Tech Stack

- Java 25
- Maven Wrapper (`./mvnw`)
- Spring Boot 4.0.5
- JUnit 5 (`spring-boot-starter-test`)

## Project Structure

- `src/main/java/com/mini/springboot/MinispringbootApplication.java`
  - Standard Spring Boot entrypoint.
- `src/main/java/com/mini/springboot/app/Main.java`
  - Intended launcher for the custom framework.
- `src/main/java/com/mini/springboot/app/HelloController.java`
  - Sample controller demonstrating routing and parameter binding.
- `src/main/java/com/mini/springboot/framework/context`
  - Bean scanning, registration, DI wiring, request context.
- `src/main/java/com/mini/springboot/framework/server/WebServer.java`
  - HTTP request handling and method invocation.
- `src/main/java/com/mini/springboot/framework/resolvers`
  - Pluggable argument resolvers (`@RequestParam`, `@PathVariable`, `@RequestBody`).
- `src/main/java/com/mini/springboot/framework/utils`
  - Trie-based route storage and helper utilities.

## Prerequisites

- JDK 25 available on `PATH`
- Linux/macOS: `chmod +x mvnw` if wrapper is not executable

## Build And Test

```bash
./mvnw clean test
```

Last validated: April 7, 2026.

## Run Spring Boot App

```bash
./mvnw spring-boot:run
```

## Run Custom Framework

`com.mini.springboot.app.Main` currently declares:

```java
static void main()
```

To make it launchable by Java tooling, change it to:

```java
public static void main(String[] args)
```

Then run with your IDE run configuration or standard Java launch command.

## Demo Endpoints (Custom Framework)

- `GET /hello`
- `GET /greet`
- `GET /greetMe?name=Mini`
- `GET /greet/{name}`
- `POST /test`

Example:

```bash
curl -X POST "http://localhost:8080/test" \
  -H "Content-Type: application/json" \
  -d '{"name":"book","price":100,"is":true,"d":4.5}'
```