package usw.suwiki.auth.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
  private static final String ADMIN_URL = "https://suwikiman.netlify.app";
  private static final String PROD_URL = "https://www.suwiki.kr";
  private static final String DEV_ENV = "http://54.180.72.97";
  private static final String LOCAL_ENV = "http://localhost";

  private static final List<String> ALLOW_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    corsConfigurationSource.registerCorsConfiguration("/**", prodCorsConfiguration());
    corsConfigurationSource.registerCorsConfiguration("/**", devCorsConfiguration());
    corsConfigurationSource.registerCorsConfiguration("/**", localCorsConfiguration());
    return corsConfigurationSource;
  }

  private CorsConfiguration prodCorsConfiguration() {
    var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(ADMIN_URL, PROD_URL, LOCAL_ENV));
    corsConfiguration.setAllowedMethods(ALLOW_METHODS);
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowCredentials(true);
    return corsConfiguration;
  }

  private CorsConfiguration devCorsConfiguration() {
    var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(DEV_ENV, LOCAL_ENV));
    corsConfiguration.setAllowedMethods(ALLOW_METHODS);
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowCredentials(true);
    return corsConfiguration;
  }

  private CorsConfiguration localCorsConfiguration() {
    var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(LOCAL_ENV));
    corsConfiguration.setAllowedMethods(ALLOW_METHODS);
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowCredentials(true);
    return corsConfiguration;
  }
}
