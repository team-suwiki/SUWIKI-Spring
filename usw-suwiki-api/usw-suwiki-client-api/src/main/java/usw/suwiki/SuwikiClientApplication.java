package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("usw.suwiki")
public class SuwikiClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(SuwikiClientApplication.class, args);
  }
}
