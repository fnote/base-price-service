package com.sysco.rps.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Logs the overall request-response latency
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 06. Jul 2020 13:20
 */
@Component
public class LatencyFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long start = System.currentTimeMillis();
        return chain.filter(exchange)
              .doFinally(aVoid ->
                    LOGGER.info("LATENCY [{}]", System.currentTimeMillis() - start)
              );
    }
}
