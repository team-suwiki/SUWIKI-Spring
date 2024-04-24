package usw.suwiki.core.secure;

import usw.suwiki.core.secure.model.Claim;

public interface TokenAgent {
  String login(Long userId);

  String reissue(String payload);

  void validateJwt(String token);

  String createAccessToken(Long userId, Claim claim);

  Long parseId(String token);

  String parseRole(String token);

  void validateRestrictedUser(String token);
}
