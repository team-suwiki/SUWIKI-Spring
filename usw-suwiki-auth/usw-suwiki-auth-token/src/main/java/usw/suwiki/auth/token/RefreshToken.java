package usw.suwiki.auth.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  public void updatePayload(String payload) {
    this.payload = payload;
  }
}
