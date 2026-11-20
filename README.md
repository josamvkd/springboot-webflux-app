# Spring Boot WebFlux Customer API

## Overview

**springboot-webflux-app** is a Proof of Concept (PoC) project demonstrating the differences between **blocking and reactive programming** using Spring Boot WebFlux and Project Reactor.

The application compares a traditional blocking customer-list endpoint with a reactive streaming endpoint using **Server-Sent Events (SSE)**.

It also demonstrates two approaches to building REST APIs with Spring WebFlux:

- **Annotation-Based Controllers** using `@RestController`.
- **Functional Routing** using `RouterFunction` and `HandlerFunction`.

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- Project Reactor (Mono and Flux)
- Server-Sent Events (SSE)
- Lombok
- JUnit

## Key Features

- Demonstrates blocking versus non-blocking processing.
- Implements reactive streaming using Flux.
- Supports Server-Sent Events (SSE).
- Demonstrates annotation-based REST controllers.
- Implements functional routing and handlers.
- Uses Mono and Flux for reactive data processing.
- Demonstrates asynchronous event emission using `delayElements()`.
- Includes Reactor examples for subscription, logging, and error handling.

## Running the Application

### Prerequisites

- Java JDK compatible with the project build configuration.
- Maven or Gradle, depending on the project setup.
- An IDE such as IntelliJ IDEA, Eclipse, or VS Code.

### Start the Application

Run the appropriate command from the project root.

| Build Tool | Start Application | Run Tests |
|---|---|---|
| Maven | `./mvnw spring-boot:run` | `./mvnw test` |
| Gradle | `./gradlew bootRun` | `./gradlew test` |

If the project does not include a build-tool wrapper, use the installed `mvn` or `gradle` command.

The examples assume the application is running at:

```text
http://localhost:8080
```

## REST API Endpoints

The application exposes both annotation-based and functional endpoints.

### Annotation-Based Controller Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customers` | Returns 10 customers as a JSON array after approximately 10 seconds. |
| GET | `/api/stream` | Streams 10 customers using SSE, approximately one customer per second. |

### Functional Routing Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/router/customers` | Returns 50 generated customers as JSON without artificial delay. |
| GET | `/router/customers/stream` | Streams 10 customers using SSE, approximately one per second. |
| GET | `/router/customer/{input}` | Retrieves a customer using a numeric ID between 1 and 50. |
| POST | `/router/customer/save` | Accepts a customer JSON object and returns its ID and name as plain text without persistence. |

**Note:** A missing numeric customer ID returns HTTP 200 with an empty response body in the current implementation.

## Testing the REST APIs Using cURL

### 1. Retrieve All Customers (Blocking)

**Endpoint:**

```http
GET /api/customers
```

**cURL:**

```bash
curl -sS http://localhost:8080/api/customers
```

**Behavior:**

- Generates 10 customer records.
- Uses `Thread.sleep(1000)` for each customer.
- Waits approximately 10 seconds before returning the complete JSON array.

### 2. Stream Customers Using SSE (Reactive)

**Endpoint:**

```http
GET /api/stream
```

**cURL:**

```bash
curl -N http://localhost:8080/api/stream
```

**Behavior:**

- Emits 10 customers as Server-Sent Events.
- Sends approximately one customer every second.
- The first customer arrives after approximately one second.
- Uses `Flux` and `delayElements()` for asynchronous emission.

The `-N` option disables cURL output buffering, allowing events to appear as they arrive.

**Sample SSE Response:**

```text
data:{"id":1,"name":"customer 1"}

data:{"id":2,"name":"customer 2"}
```

Both streaming endpoints use:

```http
Content-Type: text/event-stream
```

### 3. Retrieve Customers Using Functional Routing

**Endpoint:**

```http
GET /router/customers
```

**cURL:**

```bash
curl -sS http://localhost:8080/router/customers
```

**Behavior:**

Returns 50 generated customer records without artificial delay.

### 4. Stream Customers Using Functional Routing

**Endpoint:**

```http
GET /router/customers/stream
```

**cURL:**

```bash
curl -N http://localhost:8080/router/customers/stream
```

**Behavior:**

Streams 10 customers using SSE, approximately one customer per second.

### 5. Retrieve Customer by ID

**Endpoint:**

```http
GET /router/customer/{input}
```

**cURL:**

```bash
curl -sS http://localhost:8080/router/customer/7
```

**Sample Response:**

```json
{
  "id": 7,
  "name": "customer 7"
}
```

**Behavior:**

- Accepts a numeric customer ID.
- Searches generated customer records.
- Returns the matching customer.
- Returns HTTP 200 with an empty body when a numeric ID is not found.

### 6. Save Customer Using Functional Routing

**Endpoint:**

```http
POST /router/customer/save
```

**cURL:**

```bash
curl -sS -X POST http://localhost:8080/router/customer/save \
  -H 'Content-Type: application/json' \
  -d '{"id":51,"name":"Alex"}'
```

**Sample Response:**

```text
51 : Alex
```

**Behavior:**

- Accepts a customer JSON object.
- Reads the request body using `bodyToMono(Customer.class)`.
- Returns the customer ID and name as plain text.
- Does not persist customer information.

## Application Architecture

The application demonstrates two WebFlux programming models.

### Annotation-Based Programming Model

```text
HTTP Client
    |
    v
CustomerController
    |
    v
CustomerService
    |
    v
CustomerRepository
    |
    v
Generated Customer Data
```

**Components:**

- `CustomerController` handles annotation-based HTTP requests.
- `CustomerService` delegates customer retrieval operations.
- `CustomerRepository` generates sample customer records.

### Functional Programming Model

```text
HTTP Client
    |
    v
RouterConfig
    |
    v
CustomerHandler / CustomerStreamHandler
    |
    v
CustomerRepository / Generated Data
    |
    v
Mono<ServerResponse>
```

**Components:**

- `RouterConfig` defines functional routes.
- `CustomerHandler` processes customer lookup and POST requests.
- `CustomerStreamHandler` handles streaming operations.
- Handler methods return `Mono<ServerResponse>`.

## Understanding Blocking vs Reactive Processing

### Blocking Processing

The blocking repository method uses `Thread.sleep(1000)` while generating each customer.

```java
public static void sleepExecution(int i) {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
    }
}
```

**Execution Flow:**

```text
Request Received
      |
      v
Generate Customer 1 -> Wait 1 second
      |
      v
Generate Customer 2 -> Wait 1 second
      |
      v
            ...
      |
      v
Generate Customer 10 -> Wait 1 second
      |
      v
Return Complete JSON Array
```

**Total Execution Time:** Approximately 10 seconds.

The entire customer list is generated before the HTTP response body is returned.

### Reactive Processing

The reactive implementation uses Project Reactor to emit customer records asynchronously.

```java
public Flux<Customer> findAllCustomersStream() {
    return Flux.range(1, 10)
            .delayElements(Duration.ofSeconds(1))
            .map(i -> new Customer(i, "customer " + i));
}
```

**Execution Flow:**

```text
Request Received
      |
      v
Subscribe to Flux
      |
      v
Customer 1 -> After ~1 second
      |
      v
Customer 2 -> After ~2 seconds
      |
      v
            ...
      |
      v
Customer 10 -> After ~10 seconds
      |
      v
Stream Completed
```

**Time to First Customer:** Approximately 1 second.

**Total Stream Duration:** Approximately 10 seconds.

The reactive implementation schedules delayed emissions without calling `Thread.sleep()` on the request-processing thread.

## Blocking vs Reactive Comparison

| Feature | Blocking Endpoint | Reactive Streaming Endpoint |
|---|---|---|
| API | `/api/customers` | `/api/stream` |
| Processing | Blocking | Non-blocking delayed emission |
| Return Type | `List<Customer>` | `Flux<Customer>` |
| Delay Mechanism | `Thread.sleep()` | `delayElements()` |
| Response Format | JSON Array | Server-Sent Events |
| First Customer Received | ~10 seconds | ~1 second |
| Total Duration | ~10 seconds | ~10 seconds |
| Delivery | Entire response together | Incremental streaming |

**Important:** Reactive programming does not automatically make every operation faster. Its primary advantage in this example is that delayed emissions can be scheduled without blocking a request-processing thread.

## Understanding Mono and Flux

Project Reactor provides two primary reactive types.

### Mono

`Mono<T>` represents an asynchronous sequence that emits zero or one element.

Example:

```java
Mono<String> mono = Mono.just("Hello WebFlux");

mono.subscribe(System.out::println);
```

**Output:**

```text
Hello WebFlux
```

### Flux

`Flux<T>` represents an asynchronous sequence that emits zero to many elements.

Example:

```java
Flux<String> flux = Flux.just(
        "Java",
        "Spring Boot",
        "WebFlux"
);

flux.subscribe(System.out::println);
```

**Output:**

```text
Java
Spring Boot
WebFlux
```

### Mono vs Flux

| Feature | Mono | Flux |
|---|---|---|
| Number
