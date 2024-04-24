package usw.suwiki.auth.core.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtSecretProvider {
  private final Key key;
  private final long accessTokenExpireTime;
  private final long refreshTokenExpireTime;

  public JwtSecretProvider(
    @Value("${spring.secret-key}") String key,
    @Value("${jwt.access-duration}") long accessTokenExpireTime,
    @Value("${jwt.refresh-duration}") long refreshTokenExpireTime
  ) {
    final byte[] keyBytes = Decoders.BASE64.decode(key);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenExpireTime = accessTokenExpireTime;
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  public Key key() {
    return this.key;
  }

  public Date refreshTokenExpireTime() {
    return new Date(new Date().getTime() + this.refreshTokenExpireTime);
  }

  public Date accessTokenExpiredTime() {
    return new Date(new Date().getTime() + this.accessTokenExpireTime);
  }
}
