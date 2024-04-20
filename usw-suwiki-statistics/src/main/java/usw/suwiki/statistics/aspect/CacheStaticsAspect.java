package usw.suwiki.statistics.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import usw.suwiki.statistics.annotation.AppProfile;

@Aspect
@Component
@AppProfile
@RequiredArgsConstructor
public class CacheStaticsAspect {

  private final HttpServletRequest httpServletRequest;
  private final CacheStaticsLogger cacheStaticsLogger;

  @Around("@annotation(usw.suwiki.statistics.annotation.CacheStatics)")
  public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
    cacheStaticsLogger.getCachesStats(httpServletRequest.getRequestURI().split("/")[1]);
    return joinPoint.proceed(joinPoint.getArgs());
  }
}
