package usw.suwiki.statistics.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import usw.suwiki.statistics.annotation.AppProfile;
import usw.suwiki.statistics.annotation.Statistics;
import usw.suwiki.statistics.log.ApiLoggerService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@AppProfile
@RequiredArgsConstructor
public class MonitoringAspect {
  private final ApiLoggerService apiLoggerService;

  @Around("@annotation(usw.suwiki.statistics.annotation.Statistics) && @annotation(statistics)")
  public Object monitor(ProceedingJoinPoint joinPoint, Statistics statistics) throws Throwable {
    LocalDateTime start = LocalDateTime.now();

    try {
      return joinPoint.proceed();
    } finally {
      var request = getHttpRequest();

      LocalDateTime end = LocalDateTime.now();
      log.info("{} Api Start = {}, End = {}", request.getRequestURI(), start, end);

      apiLoggerService.logApi(statistics.target(), Duration.between(start, end).toMillis());
    }
  }

  private HttpServletRequest getHttpRequest() {
    var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return Objects.requireNonNull(attributes).getRequest();
  }
}
