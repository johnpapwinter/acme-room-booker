package com.acme.roombooker.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryMethods() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}


    @Around("repositoryMethods()")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Database Operation - {}.{} - Started with Arguments: {}",
                className, methodName, Arrays.toString(args)
        );

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed(args);
            logger.info("Database Operation - {}.{} - Completed in {}ms",
                    className, methodName, System.currentTimeMillis() - startTime
            );
            return result;
        } catch (Exception e) {
            logger.error("Database Operation - {}.{} - Failed: {}",
                    className, methodName, e.getMessage()
            );
            throw e;
        }
    }

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("API Request - {}.{} - Started with Parameters: {}",
                className, methodName, Arrays.toString(args)
        );
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed(args);
            logger.info("API Request - {}.{} - Completed in {}ms",
                    className, methodName, System.currentTimeMillis() - startTime
            );
            return result;
        } catch (Exception e) {
            logger.error("API Request - {}.{} - Failed with error: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

}
