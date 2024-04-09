package usw.suwiki.core.secure;

import usw.suwiki.core.secure.model.Claim;

public interface TokenAgent {
  String provideRefreshTokenInLogin(Long userId);

  String reissueRefreshToken(String payload);

  void validateJwt(String token);

  String createAccessToken(Long userId, Claim claim);

  String createRefreshToken(Long userId);

  Long parseId(String token);

  String parseRole(String token);

  boolean isRestrictedUser(String token);
}
