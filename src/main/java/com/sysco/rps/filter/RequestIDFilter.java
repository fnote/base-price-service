package com.sysco.rps.filter;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.UUID;

import static com.sysco.rps.common.Constants.CLIENT_ID;
import static com.sysco.rps.common.Constants.CORRELATION_ID_HEADER;
import static com.sysco.rps.common.Constants.CORRELATION_ID_KEY;

/**
 * Web Filter to extract and add IDs from the requests like correlation id and client ID  to the required contexts.
 *
 * @author Buddhika Karunatilleke
 * @author Sanjaya Amarasinghe
 * Date: 06/03/20
 */
@Component
public class RequestIDFilter implements WebFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIDFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        List<String> correlationIds = serverWebExchange.getRequest().getHeaders().get(CORRELATION_ID_HEADER);
        String correlationId;

        // if correlation ID is not there, create new one and append to MDC
        if (CollectionUtils.isEmpty(correlationIds)) {
            correlationId = UUID.randomUUID().toString();
        } else {
            correlationId = correlationIds.get(0);
        }

        List<String> clientIds = serverWebExchange.getRequest().getHeaders().get(CLIENT_ID);
        final String clientId = CollectionUtils.isEmpty(clientIds) ? "" : clientIds.get(0);

        LOGGER.info("setting correlation ID: [{}], client ID [{}]", correlationId, clientId);

        return webFilterChain.filter(serverWebExchange).subscriberContext((Context context) -> context.put(CORRELATION_ID_KEY, correlationId).put(CLIENT_ID, clientId));
    }
}
