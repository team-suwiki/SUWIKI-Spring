package usw.suwiki.auth.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.RefreshToken;
import usw.suwiki.auth.token.service.RefreshTokenService;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class JwtAgent implements TokenAgent {
  private final RefreshTokenService refreshTokenService;
  private final JwtSecretProvider jwtSecretProvider;
  private final RawParser rawParser;

  @Override
  @Transactional
  public String login(Long userId) { // todo: 하나의 토큰 묶음을 리턴할 것
    Optional<RefreshToken> wrappedRefreshToken = refreshTokenService.loadByUserId(userId);

    if (wrappedRefreshToken.isEmpty()) {
      String refreshToken = generateRefreshToken();
      refreshTokenService.save(userId, refreshToken);
      return refreshToken;
    }

    RefreshToken refreshToken = wrappedRefreshToken.get();
    if (rawParser.isExpired(refreshToken.getPayload())) {
      return refreshToken.reissue(generateRefreshToken());
    }

    return refreshToken.getPayload();
  }

  @Override
  public String reissue(String payload) {
    RefreshToken refreshToken = refreshTokenService.loadByPayload(payload);
    refreshToken.validatePayload(payload);
    return refreshToken.reissue(generateRefreshToken());
  }

  private String generateRefreshToken() {
    return Jwts.builder()
      .signWith(jwtSecretProvider.key())
      .setHeaderParam("type", "JWT")
      .setExpiration(jwtSecretProvider.refreshTokenExpireTime())
      .compact();
  }

  @Override
  public void validateJwt(String token) { // todo: 인터셉터 개편 후 삭제 예정, 호출될 일 없게 만들기
    rawParser.validate(token);
  }

  @Override
  public String createAccessToken(Long userId, Claim claim) {
    Claims claims = Jwts.claims().setSubject(claim.loginId());
    claims.putAll(Map.of("id", userId, "loginId", claim.loginId(), "role", claim.role(), "restricted", claim.restricted()));

    return Jwts.builder()
      .signWith(jwtSecretProvider.key())
      .setHeaderParam("type", "JWT")
      .setClaims(claims)
      .setExpiration(jwtSecretProvider.accessTokenExpiredTime())
      .compact();
  }

  @Override
  public Long parseId(String token) {
    return rawParser.parse(token, RawParser.Content.ID, Long.class);
  }

  @Override
  public String parseRole(String token) {
    return rawParser.parse(token, RawParser.Content.ROLE, String.class);
  }

  @Override
  public void validateRestrictedUser(String token) {
    if (Boolean.TRUE.equals(rawParser.parse(token, RawParser.Content.RESTRICTED, Boolean.class))) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }
}
