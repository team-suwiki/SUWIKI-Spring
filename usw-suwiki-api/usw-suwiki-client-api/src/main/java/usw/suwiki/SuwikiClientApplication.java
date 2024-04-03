package usw.suwiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan({
  "usw.suwiki.core.mail",
  "usw.suwiki.domain.lecture"
})
public class SuwikiClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(SuwikiClientApplication.class, args);
  }
}
