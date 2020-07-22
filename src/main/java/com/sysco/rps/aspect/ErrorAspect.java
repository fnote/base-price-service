
// TODO: @sanjayaa reconsider implementation
//package com.sysco.rps.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import reactor.core.publisher.Mono;
//
//
///**
// * This class contains an aspect to catch exceptions and throw a Mono.error.
// * Methods which are annotated with @WrapException are the joint points.
// *
// * @author Sanjaya Amarasinghe
// * @copyright (C) 2020, Sysco Corporation
// * @doc
// * @end Created : 22. Jul 2020 08:51
// */
//@Aspect
//public class ErrorAspect {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorAspect.class);
//
//    @Around("@annotation(com.sysco.rps.annotation.WrapException)")
//    public Object handleError(ProceedingJoinPoint pjp) throws Throwable {
//
//        try {
//            LOGGER.debug("Entered error aspect: {}", pjp);
//            return pjp.proceed();
//        } catch (Exception e) {
//            LOGGER.debug("Exception caught by Error Aspect");
//            return Mono.error(e);
//
//        }
//
//    }
//}
