package com.msavchuk.filter;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Adds request/response params and execution time logging for all external calls.
 */
@Aspect
@Component
@Slf4j
public class ExternalCallsAopLogger {

    @Around("execution(* com.msavchuk.controller.*.*(..))")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        inputLog(joinPoint);

        Object retVal = joinPoint.proceed();
        stopWatch.stop();
        outputLog(joinPoint, retVal, stopWatch.getTotalTimeMillis());
        return retVal;
    }

    protected void inputLog(JoinPoint joinPoint) {
        if (log.isDebugEnabled()) {
            log.info("--> Invoking '{}' with args: {}", joinPoint.getSignature(), joinPoint.getArgs());
        } else {
            log.info("--> Invoking '{}.{}' with args: {}", joinPoint.getSignature().getDeclaringType()
                                                                    .getSimpleName(),
                    joinPoint.getSignature().getName(), joinPoint.getArgs());
        }
    }

    protected void outputLog(JoinPoint joinPoint, Object result, long time) {
        if (log.isDebugEnabled()) {
            log.info("<-- Method took [{} ms] '{}' return value: {}", time, joinPoint.getSignature(), result);
        } else {
            log.info("<-- Method took [{} ms] '{}.{}' return value {} in {} ms",
                    time, joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(), result, time);
        }
    }
}
