# MiniSpringBoot

MiniSpringBoot is a learning project that currently contains two tracks:

1. A standard Spring Boot application entrypoint (`MinispringbootApplication`).
2. A custom lightweight web framework prototype built on JDK `HttpServer` with annotation-driven routing and basic dependency injection.

## Current Status

This repository is in active prototype state.

- Spring Boot context test passes.
- Custom framework now supports:
  - `@RestController` and `@Service` bean discovery
  - field injection with `@Autowired`
  - `@GetMapping` and `@PostMapping` route registration
  - path variables (`/greet/{name}`), query params, and request body binding
- Custom launcher still needs a Java-compatible `main` signature before it can be started directly.

## Tech Stack

- Java 25
- Maven Wrapper (`./mvnw`)
- Spring Boot 4.0.5
- JUnit 5 (through `spring-boot-starter-test`)

## Project Layout

- `src/main/java/com/mini/springboot/MinispringbootApplication.java`
  - Standard Spring Boot entrypoint.
- `src/main/java/com/mini/springboot/app/Main.java`
  - Intended launcher for the custom framework server.
- `src/main/java/com/mini/springboot/app/HelloController.java`
  - Demo controller with GET/POST, path variables, query param, and request body examples.
- `src/main/java/com/mini/springboot/app/GreetingService.java`
  - Demo service bean for DI.
- `src/main/java/com/mini/springboot/framework/annotations`
  - Custom annotations (`@RestController`, `@Service`, `@Autowired`, `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`).
- `src/main/java/com/mini/springboot/framework/context`
  - Bean scanning, bean creation, field injection, and route trie setup.
- `src/main/java/com/mini/springboot/framework/server/WebServer.java`
  - HTTP server and reflection-based handler invocation.
- `src/main/java/com/mini/springboot/framework/utils`
  - Trie and matching objects plus a simple JSON parser.
- `src/test/java/com/mini/springboot/MinispringbootApplicationTests.java`
  - Spring Boot context-load test.

## Prerequisites

- JDK 25 installed and available on `PATH`
- Linux/macOS: executable bit on wrapper if needed (`chmod +x mvnw`)

## Build And Test

```bash
./mvnw clean test
```

Expected outcome:
- Test suite passes.
- Spring Boot application context starts successfully.

## Run Spring Boot Entrypoint

```bash
./mvnw spring-boot:run
```

This starts `com.mini.springboot.MinispringbootApplication`.

## Run Custom Framework Entrypoint

The custom server is wired in `com.mini.springboot.app.Main`, but the method signature is currently:

```java
static void main()
```

That is not a valid Java launcher entrypoint. Change it to:

```java
public static void main(String[] args)
```

After that, the custom framework can be launched with a normal Java run configuration.

## Demo Endpoints (Custom Framework)

Once the custom launcher is fixed and running on `:8080`, the demo controller maps:

- `GET /hello`
- `GET /greet`
- `GET /greetMe?name=Mini`
- `GET /greet/{name}`
- `POST /test` with JSON body

Example request:

```bash
curl -X POST "http://localhost:8080/test" \
  -H "Content-Type: application/json" \
  -d '{"name":"book","price":100,"is":true,"d":4.5}'
```

## Suggested Next Steps

1. Fix launcher signature in `app/Main`.
2. In `WebServer`, perform route null check once before calling `.getMethod()`.
3. Add HTTP method-aware routing (or store method + path together).
4. Add integration tests for custom endpoints (`/hello`, `/greetMe`, `/greet/{name}`, `/test`).
5. Remove dead code and unused APIs/classes.

## License

No `LICENSE` file is currently present.
Add one before public distribution.

