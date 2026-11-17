Spring Boot WebFlux Customer API
springboot-webflux-app is a proof of concept that compares a blocking customer-list endpoint with a reactive stream delivered through Server-Sent Events (SSE). It shows how to expose both annotation-based controllers and functional routes in the same application.
Tech stack
- Java and Spring Boot
- Spring WebFlux and Project Reactor (Mono and Flux)
- Lombok
- JUnit for Reactor examples
Run locally
Use the JDK version configured in the project's build file. From the project root, run the command for your build tool.
Build tool	Start the app	Run the tests
Maven	./mvnw spring-boot:run	./mvnw test
Gradle	./gradlew bootRun	./gradlew test


If your project has no wrapper, use the corresponding installed command (mvn or gradle). The examples assume the app is running on port 8080.
API endpoints
Method	Path	Style	Behavior
GET	/api/customers	Controller	Returns 10 customers in one JSON array after roughly 10 seconds.
GET	/api/stream	Controller	Emits 10 customers over SSE, about one per second.
GET	/router/customers	Functional	Returns 50 generated customers as JSON, with no artificial delay.
GET	/router/customers/stream	Functional	Emits 10 customers over SSE, about one per second.
GET	/router/customer/{input}	Functional	Looks up a customer by numeric ID. IDs 1–50 exist. A missing numeric ID returns HTTP 200 with an empty body.
POST	/router/customer/save	Functional	Reads a JSON customer and echoes its ID and name as plain text. The handler performs no persistence.


Try it with curl
Compare a complete JSON response with a stream that delivers its first customer after about one second. The -N flag disables curl's output buffering so each event appears as it arrives.
curl -sS http://localhost:8080/api/customers
curl -N http://localhost:8080/api/stream
Both streaming routes use Content-Type: text/event-stream. A typical event looks like this, with a blank line after each event.
data:{"id":1,"name":"customer 1"}

data:{"id":2,"name":"customer 2"}

Try the functional routes.
curl -sS http://localhost:8080/router/customers
curl -N http://localhost:8080/router/customers/stream
curl -sS http://localhost:8080/router/customer/7
curl -sS -X POST http://localhost:8080/router/customer/save \
  -H 'Content-Type: application/json' \
  -d '{"id":51,"name":"Alex"}'
The lookup returns {"id":7,"name":"customer 7"}. The POST returns 51 : Alex.
How it works
- CustomerController delegates the annotation-based HTTP endpoints to CustomerService.
- RouterConfig maps functional routes to CustomerHandler and CustomerStreamHandler. Their methods return Mono<ServerResponse>.
- CustomerRepository generates sample customers on demand. The blocking method uses Thread.sleep(1000) for each item and builds the entire list before returning. The streaming method uses Flux.range(1, 10) and delayElements(Duration.ofSeconds(1)) to schedule each emission without sleeping on the request thread.
- CustomerHandler uses .filter(...).next() to turn the first matching customer into a Mono<Customer>. It reads POST bodies with bodyToMono(Customer.class).
Reading the timing logs
For the blocking endpoint, the service timer includes list generation and all ten sleeps. For the reactive endpoint, the timer measures only how long it takes to assemble the Flux. WebFlux subscribes later to write the response. Compare the time to the first response item to see what the client experiences. Both delayed endpoints take roughly 10 seconds to finish.
Reactor examples
MonoFluxTest illustrates subscription, logging, and error signals. Mono emits zero or one item, while Flux can emit zero to many items. In testFluxError(), the subscriber receives values through AI, then receives the error signal. That signal terminates the sequence before Azure can be emitted. These examples print Reactor signals and values to the console.
