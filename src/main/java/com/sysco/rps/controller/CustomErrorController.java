package com.sysco.rps.controller;

import com.sysco.rps.dto.ErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 29. Jul 2020 18:51
 */
@RestController
public class CustomErrorController implements ErrorWebExceptionHandler {


    private static final String PATH = "/error";

//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    Mono<ErrorDTO> error(ServerHttpRequest request, ServerHttpResponse response) {
        return Mono.just(new ErrorDTO("Sample"));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return null;
    }
}
