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

        LOGGER.info("Setting correlationId: [{}], clientId: [{}], Path: [{}]", correlationId, clientId, request.getPath());

        return webFilterChain
              .filter(serverWebExchange)
              .subscriberContext((Context context) -> context.put(CORRELATION_ID_KEY, correlationId).put(CLIENT_ID_HEADER_KEY, clientId));
    }
}
