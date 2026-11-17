package com.josamtechie.webflux.handler;

import com.josamtechie.webflux.dao.CustomerRepository;
import com.josamtechie.webflux.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerHandler
{

    private final CustomerRepository repository;

    public Mono<ServerResponse> getAllCustomers(ServerRequest request)
    {
        Flux<Customer> customerList = repository.getCustomerList();
        return ServerResponse.ok().body(customerList, Customer.class);
    }

    public Mono<ServerResponse> findCustomerById(ServerRequest request)
    {
        int customerId = Integer.parseInt(request.pathVariable("input"));
        //repository.getCustomerList().filter( c -> c.getId() == customerId).take(1).single();
        Mono<Customer> customer = repository.getCustomerList().filter(c -> c.getId() == customerId).next();
        return ServerResponse.ok().body(customer, Customer.class);
    }

    public Mono<ServerResponse> saveCustomer(ServerRequest request)
    {
        Mono<Customer> customerMono = request.bodyToMono(Customer.class);
        Mono<String> saveResponse = customerMono.map(customer -> customer.getId() + " : " + customer.getName());
        return ServerResponse.ok().body(saveResponse, String.class);
    }
}
