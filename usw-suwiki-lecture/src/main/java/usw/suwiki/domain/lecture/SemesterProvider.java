package usw.suwiki.domain.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "semester")
public class SemesterProvider {
  private final String current;

  public String current() {
    return current;
  }
}
