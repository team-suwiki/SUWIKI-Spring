package usw.suwiki.auth.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
