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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
  private static final String ADMIN = "ADMIN";

  private final JwtAgent jwtAgent;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (handler instanceof HandlerMethod handlerMethod) {
      JwtVerify jwtVerify = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), JwtVerify.class);

      if (jwtVerify != null && ADMIN.equals(jwtVerify.option())) {
        if (ADMIN.equals(extractRole(request))) {
          return true;
        }

        throw new AccountException(ExceptionType.USER_RESTRICTED);
      }
    }

    return true;
  }

  private String extractRole(HttpServletRequest request) {
    String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
    return jwtAgent.parseRole(jwt); // validate
  }
}
