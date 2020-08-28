package com.sysco.rps.controller;

import io.r2dbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 8/28/20. Fri 2020 15:02
 */
@RestController
@RequestMapping("/health/")
public class HealthController extends AbstractController {

    private Map<String, ConnectionPool> poolMap;

    @Autowired
    public HealthController(Map<String, ConnectionPool> poolMap) {
        this.poolMap = poolMap;
    }

    @GetMapping("/")
    @ResponseBody
    public Mono<Map<String, Properties>> health() {

        Map<String, Properties> k = new HashMap<>();

        poolMap.forEach((key, value) -> value.getMetrics().ifPresent(r -> k.put(key, new Properties(
              r.idleSize(),
              r.getMaxAllocatedSize(),
              r.allocatedSize(),
              r.pendingAcquireSize(),
              r.acquiredSize(),
              r.getMaxPendingAcquireSize()
        ))));

        return Mono.just(k);
    }
}

class Properties {
    Integer idleSize;
    Integer maxAllocatedSize;
    Integer allocatedSize;
    Integer pendingAquireSize;
    Integer acquiredSize;
    Integer maxPendingAcquireSize;

    public Properties(Integer idleSize, Integer maxAllocatedSize, Integer allocatedSize, Integer pendingAquireSize, Integer acquiredSize, Integer maxPendingAcquireSize) {
        this.idleSize = idleSize;
        this.maxAllocatedSize = maxAllocatedSize;
        this.allocatedSize = allocatedSize;
        this.pendingAquireSize = pendingAquireSize;
        this.acquiredSize = acquiredSize;
        this.maxPendingAcquireSize = maxPendingAcquireSize;
    }

    public Integer getIdleSize() {
        return idleSize;
    }

    public void setIdleSize(Integer idleSize) {
        this.idleSize = idleSize;
    }

    public Integer getMaxAllocatedSize() {
        return maxAllocatedSize;
    }

    public void setMaxAllocatedSize(Integer maxAllocatedSize) {
        this.maxAllocatedSize = maxAllocatedSize;
    }

    public Integer getAllocatedSize() {
        return allocatedSize;
    }

    public void setAllocatedSize(Integer allocatedSize) {
        this.allocatedSize = allocatedSize;
    }

    public Integer getPendingAquireSize() {
        return pendingAquireSize;
    }

    public void setPendingAquireSize(Integer pendingAquireSize) {
        this.pendingAquireSize = pendingAquireSize;
    }

    public Integer getAcquiredSize() {
        return acquiredSize;
    }

    public void setAcquiredSize(Integer acquiredSize) {
        this.acquiredSize = acquiredSize;
    }

    public Integer getMaxPendingAcquireSize() {
        return maxPendingAcquireSize;
    }

    public void setMaxPendingAcquireSize(Integer maxPendingAcquireSize) {
        this.maxPendingAcquireSize = maxPendingAcquireSize;
    }
}
