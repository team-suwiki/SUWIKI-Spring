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

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    corsConfigurationSource.registerCorsConfiguration("/**", defaultCorsConfiguration());
    return corsConfigurationSource;
  }

  private CorsConfiguration defaultCorsConfiguration() {
    var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(List.of(ADMIN_URL, PROD_URL));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowCredentials(true);
    return corsConfiguration;
  }
}
