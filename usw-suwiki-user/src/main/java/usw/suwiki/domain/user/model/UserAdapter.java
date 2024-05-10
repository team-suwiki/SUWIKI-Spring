package usw.suwiki.domain.user.model;

import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.User;

public record UserAdapter(
  Long id,
  String loginId,
  Role role
) {
  public static UserAdapter from(User user) {
    return new UserAdapter(user.getId(), user.getLoginId(), user.getRole());
  }
}
