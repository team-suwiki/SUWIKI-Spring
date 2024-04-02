package usw.suwiki.domain.user.model;

import usw.suwiki.core.secure.model.Claim;

public record UserClaim(
  String loginId,
  String role,
  boolean restricted
) implements Claim {
}
