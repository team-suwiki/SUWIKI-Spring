package usw.suwiki.common.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@RequiredArgsConstructor
@EnableEncryptableProperties
@ConfigurationProperties(prefix = "jasypt")
public class JasyptConfig {
  private final String password;

  @Bean("jasyptStringEncryptor")
  public StringEncryptor stringEncryptor() {
    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    encryptor.setPassword(password);
    encryptor.setIvGenerator(new NoIvGenerator());
    encryptor.setSaltGenerator(new RandomSaltGenerator());
    return encryptor;
  }
}
