package com.josamtechie.webflux.router;

import com.josamtechie.webflux.handler.CustomerHandler;
import com.josamtechie.webflux.handler.CustomerStreamHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouterConfig
{

    private final CustomerHandler handler;
    private final CustomerStreamHandler streamHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction()
    {
        return RouterFunctions.route()
                .GET("/router/customers", handler::getAllCustomers)
                .GET("/router/customers/stream", streamHandler::getCustomersStream)
                .GET("/router/customer/{input}", handler::findCustomerById)
                .POST("/router/customer/save", handler::saveCustomer)
                .build();
    }

}
