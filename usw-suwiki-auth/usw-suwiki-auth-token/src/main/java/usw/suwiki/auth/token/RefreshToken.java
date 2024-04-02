package usw.suwiki.auth.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String payload;

  @Column
  private Long userIdx;

  public void updatePayload(String payload) {
    this.payload = payload;
  }

  public static RefreshToken of(Long userIdx, String payload) {
    return builder()
      .userIdx(userIdx)
      .payload(payload)
      .build();
  }
}
