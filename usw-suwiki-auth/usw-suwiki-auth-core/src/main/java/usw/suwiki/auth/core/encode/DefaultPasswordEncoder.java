package usw.suwiki.auth.core.encode;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import usw.suwiki.core.secure.PasswordEncoder;

@Component
class DefaultPasswordEncoder implements PasswordEncoder {
  private final BCryptPasswordEncoder passwordEncoder;

  public DefaultPasswordEncoder() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
