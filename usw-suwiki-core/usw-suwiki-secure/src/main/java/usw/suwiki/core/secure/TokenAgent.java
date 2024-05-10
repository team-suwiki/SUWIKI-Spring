package usw.suwiki.core.secure;

import usw.suwiki.core.secure.model.Claim;

public interface TokenAgent {
  String login(Long userId);

  String reissue(String payload);

  String createAccessToken(Long userId, Claim claim);
}
