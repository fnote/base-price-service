// TODO : Implement or remove

//package com.sysco.rps.filter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
///**
// * @author Sanjaya Amarasinghe
// * @copyright (C) 2020, Sysco Corporation
// * @doc
// * @end Created : 13. Jun 2020 19:33
// */
//@Component
//public class LatencyFilter implements Filter {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyFilter.class);
//
//
//    @Override
//    public void doFilter(
//          ServletRequest request,
//          ServletResponse response,
//          FilterChain chain) throws IOException, ServletException {
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        HttpServletRequest req = (HttpServletRequest) request;
//
//        LOGGER.info("request received: method - {}, path - {}", req.getMethod(), req.getRequestURI());
//        chain.doFilter(request, response);
//        stopWatch.stop();
//        LOGGER.info("request completed: method - {}, path - {}, LATENCY [{}]", req.getMethod(), req.getRequestURI(),
//              stopWatch.getLastTaskTimeMillis());
//    }
//}
