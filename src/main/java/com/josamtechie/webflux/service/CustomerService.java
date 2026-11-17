package com.josamtechie.webflux.service;

import com.josamtechie.webflux.dao.CustomerRepository;
import com.josamtechie.webflux.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService
{

    private final CustomerRepository repository;

    public List<Customer> findAllCustomers()
    {
        long start = System.currentTimeMillis();
        List<Customer> customers = repository.getCustomers();
        long end = System.currentTimeMillis();
        System.out.println("Total execution time : " + (end - start));
        return customers;
    }

    public Flux<Customer> findAllCustomersStream()
    {
        long start = System.currentTimeMillis();
        Flux<Customer> customers = repository.getCustomersStream();
        long end = System.currentTimeMillis();
        System.out.println("Total execution time : " + (end - start));
        return customers;
    }
}
