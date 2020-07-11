package org.chimerax.chimeraxgateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 24-Apr-20
 * Time: 9:12 PM
 */

@Configuration
public class RoutesConfiguration {

    @Bean
    public RouteLocator routeLocator(final RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/prometheus/**")
                        .filters(rw -> rw.rewritePath("/prometheus/(?<segment>.*)", "/${segment}"))
                        .uri("lb://prometheus"))
                .route(p -> p
                        .path("/hades/**")
                        .filters(rw -> rw.rewritePath("/hades/(?<segment>.*)", "/${segment}"))
                        .uri("lb://hades"))
                .route(p -> p
                        .path("/epimetheus/**")
                        .filters(rw -> rw.rewritePath("/epimetheus/(?<segment>.*)", "/${segment}"))
                        .uri("lb://epimetheus"))
                .route(p -> p
                        .path("/csrf")
                        .uri("forward:/csrf"))
                .build();
    }
}
