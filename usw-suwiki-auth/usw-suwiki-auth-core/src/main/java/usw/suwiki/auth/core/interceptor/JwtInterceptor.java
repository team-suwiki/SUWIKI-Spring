package usw.suwiki.auth.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import usw.suwiki.auth.core.annotation.JwtVerify;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.statistics.annotation.ApiLogger;
import usw.suwiki.statistics.log.ApiLoggerService;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
  private static final String ADMIN = "ADMIN";

  private final ApiLoggerService apiLoggerService;
  private final JwtAgent jwtAgent;

  private LocalDateTime start;
  private String apiLoggerOption = "";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    startCount();

    if (handler instanceof HandlerMethod handlerMethod) {
      Method method = handlerMethod.getMethod();

      ApiLogger apiLoggerAnnotation = AnnotationUtils.findAnnotation(method, ApiLogger.class);
      JwtVerify jwtVerify = AnnotationUtils.findAnnotation(method, JwtVerify.class);

      if (apiLoggerAnnotation != null) {
        this.apiLoggerOption = apiLoggerAnnotation.option();
      } else if (jwtVerify != null) {
        String role = validateTokenAndExtractRole(request);

        if (jwtVerify.option().equals(ADMIN)) {
          if (ADMIN.equals(role)) { // role.equals(ADMIN) is nullable
            return true;
          }

          throw new AccountException(ExceptionType.USER_RESTRICTED);
        }
      }
    }

    return true;
  }

  /**
   * JWT를 request에서 추출한 뒤, getUserRole()를 호출한다. getUserRole()로 JWT를 검증하고 역할을 추출한다.
   */
  private String validateTokenAndExtractRole(HttpServletRequest request) {
    String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
    return jwtAgent.parseRole(jwt); // validate
  }

  private void startCount() {
    this.start = LocalDateTime.now();
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    LocalDateTime end = LocalDateTime.now();
    log.info("{} Api Call startTime = {}, endTime = {}", request.getRequestURI(), start, end);

    Long finalProcessingTime = calculateProcessingTime(end);
    apiLoggerService.logApi(LocalDate.now(), finalProcessingTime, apiLoggerOption);
  }

  private Long calculateProcessingTime(LocalDateTime end) {
    Duration duration = Duration.between(this.start, end);
    return duration.toMillis();
  }
}
