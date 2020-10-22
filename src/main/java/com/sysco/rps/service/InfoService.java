package com.sysco.rps.service;

import com.sysco.rps.dto.Metrics;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.PoolMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 10/21/20. Wed 2020 16:44
 */
@Service
public class InfoService {

    private Map<String, ConnectionPool> connectionPoolMap;

    @Autowired
    public InfoService(Map<String, ConnectionPool> connectionPoolMap) {
        this.connectionPoolMap = connectionPoolMap;
    }

    public Mono<List<Metrics>> getConnectionPoolInfo() {
        return Flux.fromIterable(this.connectionPoolMap.entrySet())
              .map(connectionPoolEntry -> {
                  ConnectionPool connectionPool = connectionPoolEntry.getValue();
                  Optional<PoolMetrics> optionalPoolMetrics = connectionPool.getMetrics();
                  if (optionalPoolMetrics.isPresent()) {
                      PoolMetrics poolMetrics = optionalPoolMetrics.get();
                      return new Metrics.Builder(connectionPoolEntry.getKey(), connectionPool.isDisposed())
                            .allocatedSize(poolMetrics.allocatedSize())
                            .acquiredSize(poolMetrics.acquiredSize())
                            .idleSize(poolMetrics.idleSize())
                            .pendingAcquireSize(poolMetrics.pendingAcquireSize())
                            .maxAllocatedSize(poolMetrics.getMaxAllocatedSize())
                            .maxPendingAcquireSize(poolMetrics.getMaxPendingAcquireSize())
                            .build();
                  }
                  return new Metrics(connectionPoolEntry.getKey(), connectionPool.isDisposed());
              })
              .collectList();
    }

}
