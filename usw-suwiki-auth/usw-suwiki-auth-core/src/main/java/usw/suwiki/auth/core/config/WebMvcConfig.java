package usw.suwiki.auth.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usw.suwiki.auth.core.interceptor.JwtInterceptor;
import usw.suwiki.auth.core.resolver.AuthenticatedUserArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
  private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;
  private final JwtInterceptor jwtInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
      .excludePathPatterns("/swagger-ui/**")
      .excludePathPatterns("/swagger-resources/**")
      .excludePathPatterns("/swagger-ui/**")
      .excludePathPatterns("/v3/api-docs")
      .excludePathPatterns("/index.html")
      .excludePathPatterns("/swagger-ui.html")
      .excludePathPatterns("/webjars/**")
      .addPathPatterns("/**");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(authenticatedUserArgumentResolver);
  }
}
