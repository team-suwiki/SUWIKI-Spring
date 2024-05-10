package usw.suwiki.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

  ADMIN("ADMIN", "관리자 권한"),
  USER("USER", "사용자 권한");

  private final String key;
  private final String title;

  public boolean isAdmin() {
    return this.equals(ADMIN);
  }

  public boolean isAdmin(String key) {
    return ADMIN.key.equals(key.toUpperCase());
  }
}
