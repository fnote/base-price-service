package com.sysco.rps.config.mdc;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Map;
import java.util.stream.Collectors;

import static com.sysco.rps.common.Constants.CORRELATION_ID_KEY;


/**
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 03. Jul 2020 11:46
 */

@Configuration
public class MDCContextLifterConfig {

    @PostConstruct
    private void contextOperatorHook() {
        Hooks.onEachOperator(CORRELATION_ID_KEY,
              Operators.lift((scannable, coreSubscriber) -> new MDCContextLifter<>(coreSubscriber))
        );
    }

    @PreDestroy
    private void cleanupHook() {
        Hooks.resetOnEachOperator(CORRELATION_ID_KEY);
    }
}

/***
 * Helper class that copies the reactor context to MDC
 */
class MDCContextLifter<T> implements CoreSubscriber<T> {

    private CoreSubscriber<T> coreSubscriber;

    MDCContextLifter(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = coreSubscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        coreSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T obj) {
        copyToMdc(coreSubscriber.currentContext());
        coreSubscriber.onNext(obj);
    }

    @Override
    public void onError(Throwable t) {
        copyToMdc(coreSubscriber.currentContext());
        coreSubscriber.onError(t);
    }

    @Override
    public void onComplete() {
        coreSubscriber.onComplete();
    }

    @Override
    public Context currentContext() {
        return coreSubscriber.currentContext();
    }

    /**
     * Copies the current context to the MDC, if context is empty clears the MDC.
     * State of the MDC after calling this method should be same as Reactor context's state
     * One thread-local access only.
     */
    private void copyToMdc(Context context) {

        // TODO: @sanjayaa see whether we only need to copy correlation ID
        if (!context.isEmpty()) {
            Map<String, String> map = context.stream()
                  .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

            MDC.setContextMap(map);
        } else {
            MDC.clear();
        }
    }
}
