package usw.suwiki.auth.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.user.Role;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
  private final JwtAgent jwtAgent;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    var authorize = resolve(handler);

    if (authorize != null) {
      String token = request.getHeader(HttpHeaders.AUTHORIZATION);
      validateAccessIfAdmin(authorize.role(), token);
      setAuthentication(token);
    }

    return true;
  }

  private Authorize resolve(Object handler) {
    var method = ((HandlerMethod) handler).getMethod();
    return method.getAnnotation(Authorize.class);
  }

  private void validateAccessIfAdmin(Role role, String token) {
    if (role.isAdmin() && jwtAgent.isNotAdmin(token)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }

  private void setAuthentication(String token) {
    var authentication = jwtAgent.parseAuthentication(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
