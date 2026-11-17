package com.josamtechie.webflux.handler;

import com.josamtechie.webflux.dao.CustomerRepository;
import com.josamtechie.webflux.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerStreamHandler
{

    private final CustomerRepository repository;

    public Mono<ServerResponse> getCustomersStream(ServerRequest request)
    {
        Flux<Customer> customersStream = repository.getCustomersStream();
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(customersStream, Customer.class);
    }
}
