package com.sysco.rps.repository.common;

import org.springframework.data.r2dbc.connectionfactory.lookup.AbstractRoutingConnectionFactory;
import reactor.core.publisher.Mono;

import static com.sysco.rps.common.Constants.ROUTING_KEY;

/**
 * @author Tharuka Jayalath
 * (C) 2019, Sysco Labs
 * Created: 7/6/20. Mon 2020 09:48
 */
public class RoutingConnectionFactory extends AbstractRoutingConnectionFactory {

    @Override
    protected Mono<Object> determineCurrentLookupKey() {
        return Mono.subscriberContext().filter(it -> it.hasKey(ROUTING_KEY)).map(it -> it.get(ROUTING_KEY));
    }

}
