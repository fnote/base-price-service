package com.sysco.rps.config;

import io.r2dbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 8/28/20. Fri 2020 23:53
 */
@Component
public class R2dbcPoolHealthIndicator implements ReactiveHealthIndicator {

    private Map<String, ConnectionPool> connectionPoolMap;

    @Autowired
    public R2dbcPoolHealthIndicator(Map<String, ConnectionPool> connectionPoolMap) {
        this.connectionPoolMap = connectionPoolMap;
    }

    /**
     * Provide the indicator of health.
     * Verifies whether the connection pool status is stable or not by analyzing
     * the idle and pending acquire connection counts in the pool
     * if the pool status is stable then the idle connection count should be zero
     * when pending acquire size is a positive value
     *
     * @return a {@link Mono} that provides the {@link Health}
     */
    @Override
    public Mono<Health> health() {
        return Flux.fromIterable(connectionPoolMap.values())
              .map(ConnectionPool::getMetrics)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .filter(metrics -> (metrics.idleSize() > 0 && metrics.pendingAcquireSize() > 0))
              .collectList()
              .flatMap(metricsList -> {
                  if (metricsList.isEmpty()) {
                      return Mono.just(new Health.Builder().up().build());
                  }
                  return Mono.just(new Health.Builder().down().build());
              });
    }
}
