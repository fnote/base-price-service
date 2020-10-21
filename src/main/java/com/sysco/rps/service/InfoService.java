package com.sysco.rps.service;

import com.sysco.rps.dto.Metrics;
import io.r2dbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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
              .map( connectionPoolEntry -> {
                  Metrics metrics = new Metrics();
                  connectionPoolEntry.getValue().getMetrics().ifPresent(poolMetrics -> {
                      metrics.setPoolId(connectionPoolEntry.getKey());
                      metrics.setAcquiredSize(poolMetrics.acquiredSize());
                      metrics.setAllocatedSize(poolMetrics.allocatedSize());
                      metrics.setIdleSize(poolMetrics.idleSize());
                      metrics.setPendingAcquireSize(poolMetrics.pendingAcquireSize());
                      metrics.setMaxAllocatedSize(poolMetrics.getMaxAllocatedSize());
                      metrics.setMaxPendingAcquireSize(poolMetrics.getMaxPendingAcquireSize());
                      metrics.setDisposed(connectionPoolEntry.getValue().isDisposed());
                  });
                  return metrics;
              }).collectList();
    }

}
