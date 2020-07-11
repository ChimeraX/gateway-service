package org.chimerax.chimeraxgateway.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.UUID;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 11-Jun-20
 * Time: 8:10 AM
 */
@Component
@Order(2)
@AllArgsConstructor
public class JWTGatewayFilter implements GlobalFilter {

    private static final String COOKIE_NAME = "token";
    private static final String HEADER_NAME = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        val cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);
        System.out.println(cookie);
        if (cookie != null) {
            val request = exchange.getRequest()
                    .mutate()
                    .header(HEADER_NAME, "Bearer " + cookie.getValue())
                    .build();
            exchange = exchange.mutate().request(request).build();
        }
        return chain.filter(exchange);
    }

}
