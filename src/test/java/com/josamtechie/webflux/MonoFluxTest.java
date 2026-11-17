package com.josamtechie.webflux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoFluxTest
{
    @Test
    public void testMono()
    {
        Mono<String> monoString = Mono.just("josamtechie").log();
        monoString.subscribe(System.out::println);
    }

    @Test
    public void testMonoError()
    {
        Mono<?> monoString = Mono.just("welcome")
                .then(Mono.error(new RuntimeException("Exception Occurred in Mono !!")))
                .log();
        monoString.subscribe(System.out::println, e -> System.out.println(e.getMessage()));
    }

    @Test
    public void testFlux()
    {
        Flux<String> fluxString = Flux.just("CoreJava", "Hibernate", "SpringBoot", "AWS", "Jenkins", "Kafka")
                                    .concatWithValues("AI")
                                    .log();
        fluxString.subscribe(System.out::println);
    }

    @Test
    public void testFluxError()
    {
        Flux<String> fluxString = Flux.just("CoreJava", "Hibernate", "SpringBoot", "AWS", "Jenkins", "Kafka")
                .concatWithValues("AI")
                .concatWith(Flux.error(new RuntimeException("Error occurred in Flux !!")))
                .concatWithValues("Azure")
                .log();
        fluxString.subscribe(System.out::println, e -> System.out.println(e.getMessage()));
    }
}
