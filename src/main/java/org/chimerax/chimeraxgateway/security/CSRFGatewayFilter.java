package org.chimerax.chimeraxgateway.security;

import io.netty.handler.codec.base64.Base64Encoder;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 11-Jun-20
 * Time: 8:10 AM
 */
//@Component
@Order(0)
@AllArgsConstructor
public class CSRFGatewayFilter implements GlobalFilter {

    private static final String COOKIE_NAME = "_csrf";
    private RSA rsa;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        val path = exchange.getRequest().getPath().value();
        val cookie = getCookie(exchange.getRequest());
        if (path.equals("/csrf")) {
            this.addCookie(exchange);
            return chain.filter(exchange);
        } else {
            if (cookie != null && !cookie.getValue().equals("")) {
                if (isValid(cookie.getValue())) {
                    return chain.filter(exchange);
                } else {
                    return Mono.empty();
                }
            } else {
                return Mono.empty();
            }
        }

    }

    private HttpCookie getCookie(final ServerHttpRequest request) {
        return request.getCookies().getFirst(COOKIE_NAME);
    }

    private static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @SneakyThrows
    private void addCookie(final ServerWebExchange exchange) {
        val value = encode(generate());
        val cookie = ResponseCookie
                .from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMinutes(30))
                .build();
        exchange.getResponse().addCookie(cookie);
    }

    private boolean isValid(final String csrf) {
        try {
            decode(csrf);
            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    private static String encode(String s) {
        s = Base64.getEncoder().encodeToString(s.getBytes());
        s = URLEncoder.encode(s, "utf-8");
        return s;
    }

    @SneakyThrows
    private static String decode(String s) {
        s = URLDecoder.decode(s, "utf-8");
        byte[] bytes = Base64.getDecoder().decode(s.getBytes());
        return new String(bytes);
    }
}
