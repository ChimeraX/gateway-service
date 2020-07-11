package org.chimerax.chimeraxgateway.security;

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

import java.time.Duration;
import java.util.UUID;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 11-Jun-20
 * Time: 8:10 AM
 */
//@Component
@Order(1)
public class DoubleSubmitCookieFilter implements GlobalFilter {
    private static final String COOKIE_NAME = "_csrf";
    private static final String HEADER_NAME = "_csrf";

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        val request = exchange.getRequest();
        val path = exchange.getRequest().getPath().value();
        val header = getHeader(request);
        val cookie = getCookie(request);
        if (!path.equals("/csrf")) {
            if (cookie != null) {
                if (header.equals(cookie.getValue())) {
                    this.addCookie(exchange);
                    return chain.filter(exchange);
                } else {
                    return Mono.empty();
                }
            } else {
                return Mono.empty();
            }
        } else {
            this.addCookie(exchange);
            return Mono.empty();
        }
    }

    private String getHeader(final ServerHttpRequest request) {
        return request.getHeaders().getFirst(HEADER_NAME);
    }

    private HttpCookie getCookie(final ServerHttpRequest request) {
        return request.getCookies().getFirst(COOKIE_NAME);
    }

    private String generate() {
        return UUID.randomUUID().toString();
    }

    private void addCookie(final ServerWebExchange exchange) {
        val cookie = ResponseCookie
                .from(COOKIE_NAME, generate())
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();
        exchange.getResponse().addCookie(cookie);
    }
}
