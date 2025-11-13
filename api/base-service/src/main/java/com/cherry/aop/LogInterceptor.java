package com.cherry.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.UUID;


@Aspect
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截
     */
    @Around("execution(* com.cherry.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // 需要统计每一个service实现的执行时间，如果执行时间太久，则进行error级别的日志输出

        // long begin = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();


        String pointName = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();
        stopWatch.start("执行主业务：" + pointName);
        Object proceed = joinPoint.proceed();
        stopWatch.stop();

        // stopWatch.start("执行其他业务1001");
        // Thread.sleep(250);
        // stopWatch.stop();
        //
        // stopWatch.start("执行其他业务2002");
        // Thread.sleep(350);
        // stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());
        log.info("任务总数：" + stopWatch.getTaskCount());
        log.info("任务执行总时间：" + stopWatch.getTotalTimeMillis() + "ms");

        // long end = System.currentTimeMillis();
        // long takeTimes = end - begin;

        long takeTimes = stopWatch.getTotalTimeMillis();
        if (takeTimes > 3000) {
            log.error("执行位置{}，执行时间太长了，耗费了{}毫秒", pointName, takeTimes);
        } else if (takeTimes > 2000) {
            log.warn("执行位置{}，执行时间稍微有点长，耗费了{}毫秒", pointName, takeTimes);
        } else {
            log.info("执行位置{}，执行时间正常，耗费了{}毫秒", pointName, takeTimes);
        }

        return proceed;
    }
}

