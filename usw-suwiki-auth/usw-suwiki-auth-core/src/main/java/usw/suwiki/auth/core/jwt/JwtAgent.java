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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    Optional<RefreshToken> wrappedRefreshToken =
      refreshTokenCRUDService.loadRefreshTokenFromUserIdx(userId);

    if (wrappedRefreshToken.isEmpty()) {
      return createRefreshToken(userId);
    }

    RefreshToken refreshToken = wrappedRefreshToken.get();
    if (isRefreshTokenExpired(refreshToken.getPayload())) {
      String payload = reIssueRefreshToken(refreshToken);
      wrappedRefreshToken.get().updatePayload(payload);
      return payload;
    }
    return refreshToken.getPayload();
  }

  @Override
  @Transactional
  public String reissueRefreshToken(String payload) {
    RefreshToken refreshToken = refreshTokenCRUDService.loadRefreshTokenFromPayload(payload);

    if (!refreshToken.getPayload().equals(payload)) {
      throw new AccountException(ExceptionType.TOKEN_IS_BROKEN);
    }

    String newPayload = reIssueRefreshToken(refreshToken);
    refreshToken.updatePayload(newPayload);
    return newPayload;
  }

  private String reIssueRefreshToken(RefreshToken refreshToken) {
    refreshToken.updatePayload(buildRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime)));
    return refreshToken.getPayload();
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
    return buildAccessToken(
      setAccessTokenClaimsByUser(userId, claim),
      new Date(new Date().getTime() + accessTokenExpireTime)
    );
  }

  @Override
  public String createRefreshToken(Long userId) {
    String refreshToken = buildRefreshToken(new Date(new Date().getTime() + refreshTokenExpireTime));
    refreshTokenCRUDService.save(new RefreshToken(userId, refreshToken));
    return refreshToken;
  }

  @Override
  public Long getId(String token) {
    validateJwt(token);
    Object id = Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("id");
    return Long.valueOf(String.valueOf(id));
  }

  @Override
  public String getUserRole(String token) {
    validateJwt(token);
    return (String) Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("role");
  }

  @Override
  public Boolean getUserIsRestricted(String token) {
    validateJwt(token);
    return (Boolean) Jwts.parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody().get("restricted");
  }

  private Boolean isRefreshTokenExpired(String payload) {
    Date date;
    try {
      date = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(payload)
        .getBody().getExpiration();
    } catch (ExpiredJwtException expiredJwtException) {
      return true;
    }
    return false;
  }

  private Claims resolveBodyFromRefreshToken(String refreshToken) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(refreshToken)
        .getBody();
    } catch (ExpiredJwtException expiredJwtException) {
      throw new AccountException(ExceptionType.TOKEN_IS_EXPIRED);
    }
  }

  // TODO: 만료시한까지 7일 이하 남았을 때만 -> 1/4 남았을 때만.
  private static boolean isRefreshTokenNotExpired(Date date) {
    LocalDateTime tokenExpiredAt = date
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();

    LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);
    return tokenExpiredAt.isBefore(LocalDateTime.now());
  }

  private Key getSigningKey() {
    final byte[] keyBytes = Decoders.BASE64.decode(this.key);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims setAccessTokenClaimsByUser(Long userId, Claim claim) {
    Claims claims = Jwts.claims();
    claims.setSubject(claim.loginId());
    claims.put("id", userId);
    claims.put("loginId", claim.loginId());
    claims.put("role", claim.role());
    claims.put("restricted", claim.restricted());
    return claims;
  }

  // TODO: body 값 정보 추가하기 (type:ac,re , subject:유저 식별자)
  private String buildAccessToken(Claims claims, Date accessTokenExpireIn) {
    return Jwts.builder()
      .signWith(getSigningKey())
      .setHeaderParam("type", "JWT")
      .setClaims(claims)
      .setExpiration(accessTokenExpireIn)
      .compact();
  }

  private String buildRefreshToken(Date refreshTokenExpireIn) {
    return Jwts.builder()
      .signWith(getSigningKey())
      .setHeaderParam("type", "JWT")
      .setExpiration(refreshTokenExpireIn)
      .compact();
  }
}
