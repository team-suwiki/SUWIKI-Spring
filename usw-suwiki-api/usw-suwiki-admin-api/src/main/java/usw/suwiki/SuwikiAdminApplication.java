package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("usw.suwiki")
public class SuwikiAdminApplication {
  public static void main(String[] args) {
    SpringApplication.run(SuwikiAdminApplication.class, args);
  }
}
