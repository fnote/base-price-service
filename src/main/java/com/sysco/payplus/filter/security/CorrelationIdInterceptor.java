package com.sysco.payplus.filter.security;


import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

/**
 * Request interceptor for correlation id resolving. If request has 'correlation-id' header
 *
 * @author ralw0871
 */
@Component
public class CorrelationIdInterceptor implements HandlerInterceptor {

    /**
     * @param request  : [HttpServletRequest] HTTP Request object
     * @param response : [HttpServletResponse] HTTP Response object
     * @param handler  : [Object] Chosen handler to execute
     * @return : [Boolean] Status of the pre handler execution
     *
     */

    private static final String CORRELATION_ID="X-Correlation-Id";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_ID))
                .orElse(UUID.randomUUID().toString());
        MDC.clear();
        MDC.put(CORRELATION_ID, correlationId);
        ThreadLocalCache.put(CORRELATION_ID, correlationId);
        return true;
    }
}
