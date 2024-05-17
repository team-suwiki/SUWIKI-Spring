package usw.suwiki.auth.core.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.core.exception.BaseException;
import usw.suwiki.core.exception.ExceptionType;

import java.lang.reflect.Method;
import java.util.Objects;

@Component
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Long.class) && parameter.hasParameterAnnotation(Authenticated.class);
  }

  @Override
  public Long resolveArgument(
    MethodParameter parameter,
    ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest,
    WebDataBinderFactory binderFactory
  ) {
    validateAuthorization(parameter);
    return resolveIdFromContext();
  }

  private void validateAuthorization(MethodParameter parameter) {
    Method method = Objects.requireNonNull(parameter.getMethod());

    if (method.getAnnotation(Authorize.class) == null) {
      throw new BaseException(ExceptionType.AUTHORIZATION_NOT_PROCESSED);
    }
  }

  private Long resolveIdFromContext() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var principal = (User) authentication.getPrincipal();
    return Long.parseLong(principal.getUsername()); // username : 사용자의 ID 값
  }
}
