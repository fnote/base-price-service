package com.sysco.rps.filter;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.util.List;
import java.util.UUID;

import static com.sysco.rps.common.Constants.CLIENT_ID_HEADER_KEY;
import static com.sysco.rps.common.Constants.CORRELATION_ID_HEADER_KEY;
import static com.sysco.rps.common.Constants.CORRELATION_ID_KEY;
import static com.sysco.rps.common.Constants.ALB_SOURCE_IP_HEADER_KEY;

/**
 * Web Filter to extract and add IDs from the requests like correlation id and client ID  to the required contexts.
 *
 * @author Buddhika Karunatilleke
 * @author Sanjaya Amarasinghe
 * @author Tharuka Jayalath
 * Date: 06/03/20
 */
@Component
public class RequestIDFilter implements WebFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIDFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        List<String> correlationIds = serverWebExchange.getRequest().getHeaders().get(CORRELATION_ID_HEADER_KEY);

        if (CollectionUtils.isEmpty(correlationIds)) {
            return Mono.defer(() -> Mono.just(UUID.randomUUID().toString()))
                  .subscribeOn(Schedulers.boundedElastic())
                  .publishOn(Schedulers.parallel())
                  .flatMap(correlationId -> updateContextWithRequestIdentifiers(serverWebExchange, webFilterChain, correlationId));
        }
        return updateContextWithRequestIdentifiers(serverWebExchange, webFilterChain, correlationIds.get(0));
    }

    private Mono<Void> updateContextWithRequestIdentifiers(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain, String correlationId) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        List<String> clientIds = request.getHeaders().get(CLIENT_ID_HEADER_KEY);
        final String clientId = CollectionUtils.isEmpty(clientIds) ? "" : clientIds.get(0);
        String ipAddress = extractIPAddress(serverWebExchange);

        LOGGER.info("Setting correlationId: [{}], clientId: [{}], Path: [{}], clientIP[{}]", correlationId, clientId, request.getPath(), ipAddress);

        return webFilterChain
              .filter(serverWebExchange)
              .subscriberContext((Context context) -> context.put(CORRELATION_ID_KEY, correlationId).put(CLIENT_ID_HEADER_KEY, clientId));
    }

    private String extractIPAddress(ServerWebExchange exchange) {
        String ipAddress = null;
        if (exchange.getRequest().getHeaders().containsKey(ALB_SOURCE_IP_HEADER_KEY) && !exchange.getRequest().getHeaders().get(ALB_SOURCE_IP_HEADER_KEY).isEmpty()) {
            ipAddress = exchange.getRequest().getHeaders().get(ALB_SOURCE_IP_HEADER_KEY).get(0);
        }
        return ipAddress;
    }
}
