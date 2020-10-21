package com.sysco.rps.service;

import io.r2dbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<Map<String, ConnectionPool>> getConnectionPoolInfo() {
        return Mono.just(this.connectionPoolMap);
    }

}
