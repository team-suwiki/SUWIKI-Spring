package usw.suwiki.core.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "server")
class ServerProperties {
  private static final String CONFIRMATION_TOKEN_URL = "/v2/confirmation-token/verify/?token=";

  private final int port;
  private final String domain;

  String redirectUrl(String token) {
    return domain + CONFIRMATION_TOKEN_URL + token;
  }
}
