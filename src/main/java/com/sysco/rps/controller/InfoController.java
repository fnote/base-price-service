package com.sysco.rps.controller;

import com.sysco.rps.service.InfoService;
import io.r2dbc.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 10/21/20. Wed 2020 16:43
 */
@RestController
@RequestMapping("/info")
public class InfoController {

    private InfoService infoService;

    @Autowired
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/connection-pool")
    public Mono<Map<String, ConnectionPool>> getConnectionPoolInfo() {
        return infoService.getConnectionPoolInfo();
    }

}
