package com.sysco.rps.controller;

import com.sysco.rps.dto.Metrics;
import com.sysco.rps.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controller class that exposes the application status endpoints
 *
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 10/21/20. Wed 2020 16:43
 */
@RestController
@RequestMapping("/support")
public class SupportController {

    private final SupportService supportService;

    @Autowired
    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    /**
     * Exposes database connection-pool information
     *
     * @return {@link Mono} of list of Metrics
     */
    @GetMapping("/connection-pool-metrics")
    public Mono<List<Metrics>> getConnectionPoolInfo() {
        return supportService.getConnectionPoolInfo();
    }

}
