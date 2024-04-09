package usw.suwiki.auth.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.RefreshToken;
import usw.suwiki.auth.token.service.RefreshTokenCRUDService;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAgent implements TokenAgent {

  @Value("${spring.secret-key}")
  private String key;

  @Value("${jwt.access-duration}")
  public long accessTokenExpireTime;

  @Value("${jwt.refresh-duration}")
  public long refreshTokenExpireTime;

  private final RefreshTokenCRUDService refreshTokenCRUDService;

  @Override
  @Transactional
  public String provideRefreshTokenInLogin(Long userId) {
    Optional<RefreshToken> wrappedRefreshToken = refreshTokenCRUDService.loadRefreshTokenFromUserIdx(userId);

    if (wrappedRefreshToken.isEmpty()) {
      return createRefreshToken(userId);
    }

    RefreshToken refreshToken = wrappedRefreshToken.get();
    if (isRefreshTokenExpired(refreshToken.getPayload())) {
      String payload = generateRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime));
      refreshToken.reissue(payload);
      return payload;
    }

    return refreshToken.getPayload();
  }

  @Override
  @Transactional
  public String reissueRefreshToken(String payload) {
    RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload);
    refreshToken.validatePayload(payload);

    String newPayload = generateRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime));
    refreshToken.reissue(newPayload);
    return newPayload;
  }

  public void validateJwt(String token) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSigningKey()).build()
        .parseClaimsJws(token);
    } catch (MalformedJwtException | IllegalArgumentException ex) {
      throw new AccountException(ExceptionType.LOGIN_REQUIRED);
    } catch (ExpiredJwtException exception) {
      throw new AccountException(ExceptionType.TOKEN_IS_EXPIRED);
    }
  }

  @Override
  public String createAccessToken(Long userId, Claim claim) {
    Claims claims = Jwts.claims().setSubject(claim.loginId());
    claims.putAll(Map.of("id", userId, "loginId", claim.loginId(), "role", claim.role(), "restricted", claim.restricted()));

    return Jwts.builder()
      .signWith(getSigningKey())
      .setHeaderParam("type", "JWT")
      .setClaims(claims)
      .setExpiration(new Date(new Date().getTime() + accessTokenExpireTime))
      .compact();
  }

  @Override
  public String createRefreshToken(Long userId) {
    String refreshToken = generateRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime));
    refreshTokenCRUDService.save(new RefreshToken(userId, refreshToken));
    return refreshToken;
  }

  @Override
  public Long parseId(String token) {
    validateJwt(token);
    Object id = Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("id");
    return Long.valueOf(String.valueOf(id));
  }

  @Override
  public String parseRole(String token) {
    validateJwt(token);
    return (String) Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("role");
  }

  @Override
  public boolean isRestrictedUser(String token) {
    validateJwt(token);
    return (boolean) Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("restricted");
  }

  private boolean isRefreshTokenExpired(String payload) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(payload)
        .getBody().getExpiration();
    } catch (ExpiredJwtException expiredJwtException) {
      return true;
    }
    return false;
  }

  private Key getSigningKey() {
    final byte[] keyBytes = Decoders.BASE64.decode(this.key);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String generateRefreshToken(Date refreshTokenExpireIn) {
    return Jwts.builder()
      .signWith(getSigningKey())
      .setHeaderParam("type", "JWT")
      .setExpiration(refreshTokenExpireIn)
      .compact();
  }
}
