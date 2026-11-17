package com.josamtechie.webflux.controller;

import com.josamtechie.webflux.model.Customer;
import com.josamtechie.webflux.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomerController
{
    private final CustomerService service;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers()
    {
        return service.findAllCustomers();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Customer> getAllCustomersStream()
    {
        return service.findAllCustomersStream();
    }
}
