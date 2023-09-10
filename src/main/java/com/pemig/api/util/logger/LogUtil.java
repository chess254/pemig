package com.pemig.api.util.logger;

import static com.pemig.api.util.logger.MethodLoggingMessages.msg;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogUtil {

  @Before("execution(* (@com.pemig.api.util.logger.Logs *..*).*(..))")
  public void beforeCallingAnyMethod(JoinPoint joinPoint) {
    log.info(msg(LogPoint.BEGIN, joinPoint));
  }

  @AfterReturning("execution(* (@com.pemig.api.util.logger.Logs *..*).*(..))")
  public void afterCallingAnyMethod(JoinPoint joinPoint) {
    log.info(msg(LogPoint.END, joinPoint));
  }

  @AfterThrowing(
      value = ("execution(* (@com.pemig.api.util.logger.Logs *..*).*(..))"), throwing = "ex")
  public void afterCallingAMethodThrows(JoinPoint joinPoint, Throwable ex) {
    log.warn(msg(joinPoint, ex.getClass()));
  }
}
