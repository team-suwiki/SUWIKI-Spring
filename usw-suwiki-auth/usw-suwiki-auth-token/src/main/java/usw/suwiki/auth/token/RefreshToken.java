package usw.suwiki.auth.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long userIdx;

  @Column
  private String payload;

  public RefreshToken(Long userIdx, String payload) {
    this.userIdx = userIdx;
    this.payload = payload;
  }

  public void reissue(String payload) {
    this.payload = payload;
  }

  public void validatePayload(String payload) {
    if (!Objects.equals(this.payload, payload)) {
      throw new AccountException(ExceptionType.TOKEN_IS_BROKEN);
    }
  }
}
