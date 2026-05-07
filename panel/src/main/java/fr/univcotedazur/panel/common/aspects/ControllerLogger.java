package fr.univcotedazur.panel.common.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ControllerLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerLogger.class);

    @Pointcut("execution(public * fr.univ_cotedazur.gate.*.controllers..*(..))")
    private void allControllerMethods() {
    } // This enables to attach the pointcut to a method name we can reuse below

    @Before("allControllerMethods()")
    public void logMethodNameAndParametersAtEntry(JoinPoint joinPoint) {
        LOG.info("[{}] - Called {} with args {}",
                joinPoint.getThis().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterThrowing(pointcut = "allControllerMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        LOG.warn("[{}] - ERROR in {} : {}",
                joinPoint.getThis().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getMessage()
        );
    }

}
