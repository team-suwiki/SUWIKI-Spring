package usw.suwiki.domain.lecture.major;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteMajor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userIdx;

  @Column
  private String majorType;

  public FavoriteMajor(Long userIdx, String majorType) {
    this.userIdx = userIdx;
    this.majorType = majorType;
  }
}
