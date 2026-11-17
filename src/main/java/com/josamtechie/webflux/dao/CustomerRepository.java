package com.josamtechie.webflux.dao;

import com.josamtechie.webflux.model.Customer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class CustomerRepository
{

    public static void sleepExecution(int i)
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Customer> getCustomers()
    {
        return IntStream.rangeClosed(1, 10)
                .peek(CustomerRepository::sleepExecution)
                .peek(i -> System.out.println("Processing count : " + i))
                .mapToObj(i -> new Customer(i, "customer " + i))
                .toList();
    }

    public Flux<Customer> getCustomersStream()
    {
        return Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println("Stream processing count : " + i))
                .map(i -> new Customer(i, "customer " + i));
    }


    public Flux<Customer> getCustomerList()
    {
        return Flux.range(1, 50)
                .doOnNext(i -> System.out.println("Stream processing count : " + i))
                .map(i -> new Customer(i, "customer " + i));
    }
}
