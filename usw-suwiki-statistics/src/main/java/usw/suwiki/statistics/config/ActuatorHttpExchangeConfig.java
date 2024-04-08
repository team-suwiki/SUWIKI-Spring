package usw.suwiki.statistics.config;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorHttpExchangeConfig {

  @Bean
  public InMemoryHttpExchangeRepository inMemoryHttpTraceRepository() {
    return new InMemoryHttpExchangeRepository();
  }
}
