package com.logicea.cardtask.util.logger;

import static com.logicea.cardtask.util.logger.MethodLoggingMessages.msg;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogUtil {

  @Before("execution(* (@com.logicea.cardtask.util.logger.Logs *..*).*(..))")
  public void beforeCallingAnyMethod(JoinPoint joinPoint) {
    log.info(msg(LogPoint.BEGIN, joinPoint));
  }

  @AfterReturning("execution(* (@com.logicea.cardtask.util.logger.Logs *..*).*(..))")
  public void afterCallingAnyMethod(JoinPoint joinPoint) {
    log.info(msg(LogPoint.END, joinPoint));
  }

  @AfterThrowing(
      value = ("execution(* (@com.logicea.cardtask.util.logger.Logs *..*).*(..))"), throwing = "ex")
  public void afterCallingAMethodThrows(JoinPoint joinPoint, Throwable ex) {
    log.warn(msg(joinPoint, ex.getClass()));
  }
}
