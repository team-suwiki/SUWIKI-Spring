package usw.suwiki.auth.core.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
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

import static java.lang.Boolean.TRUE;
import static usw.suwiki.auth.core.jwt.RawParser.Content;
import static usw.suwiki.domain.user.Role.ADMIN;

@Component
@RequiredArgsConstructor
public class JwtAgent implements TokenAgent { // todo: public 이지만 외부로 노출을 감출 것.
  private final RefreshTokenService refreshTokenService;

  private final UserDetailsService userDetailsService;
  private final JwtSecretProvider jwtSecretProvider;
  private final RawParser rawParser;

  public Authentication parseAuthentication(String token) {
    validateRestricted(token);
    var loginId = rawParser.parse(token, Content.LOGIN_ID, String.class);
    var userDetails = userDetailsService.loadUserByUsername(loginId);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  private void validateRestricted(String token) {
    if (TRUE.equals(rawParser.parse(token, Content.RESTRICTED, Boolean.class))) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }

  @Transactional
  @Override
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

  @Transactional
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

  public String parseRole(String token) {
    return rawParser.parse(token, Content.ROLE, String.class);
  }

  public boolean isNotAdmin(String token) {
    return token != null && ADMIN.isAdmin(parseRole(token));
  }
}
