package ca.gbc.apigateway.routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;

@Slf4j
@Configuration
public class Routes {

    @Value("${services.product.url}")
    private String productServiceUrl;

    @Value("${services.order.url}")
    private String orderServiceUrl;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;


    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Initializing product service route with URL: {}", productServiceUrl );

        return GatewayRouterFunctions.route("product-service")
                .route(RequestPredicates.path("/api/product"), request -> {
                    log.info("Received request for product service: {}", request.uri());
                    ServerResponse response = HandlerFunctions.http(productServiceUrl).handle(request);
                    log.info("Response status: {}", response.statusCode());
                    return response;

                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallbackRoute")) )
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Initializing order service route with URL: {}", orderServiceUrl );

        return GatewayRouterFunctions.route("order-service")
                .route(RequestPredicates.path("/api/order"), request -> {
                    log.info("Received request for order service: {}", request.uri());
                        ServerResponse response = HandlerFunctions.http(orderServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;

                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallbackRoute")) )
                .build();

    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        log.info("Initializing order service route with URL: {}", inventoryServiceUrl );

        return GatewayRouterFunctions.route("inventory-service")
                .route(RequestPredicates.path("/api/inventory"), request -> {
                    log.info("Received request for inventory service: {}", request.uri());
                        ServerResponse response = HandlerFunctions.http(inventoryServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallbackRoute")) )
                .build();

    }



    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        log.info("Forwarding Swagger request for product-service to: {}", productServiceUrl + "/api-docs");

        return GatewayRouterFunctions.route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                        HandlerFunctions.http(productServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        log.info("Forwarding Swagger request for order-service to: {}", orderServiceUrl + "/api-docs");

        return GatewayRouterFunctions.route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                        HandlerFunctions.http(orderServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        log.info("Forwarding Swagger request for inventory-service to: {}", inventoryServiceUrl + "/api-docs");

        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                        HandlerFunctions.http(inventoryServiceUrl))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return GatewayRouterFunctions.route("fallBackRoute")
                .route(request -> !(request.path().startsWith("/swagger-ui") ||
                                request.path().startsWith("/api-docs") ||
                                request.path().startsWith("/swagger-resources") ||
                                request.path().startsWith("/webjars") ||
                                request.path().startsWith("/api-docs/swagger-config")), // Exclude this path
                        request -> {
                            log.warn("Fallback triggered for path: {}", request.path());
                            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                    .body("Service is Temporarily Unavailable, please try again later.");
                        })
                .build();
    }




}


