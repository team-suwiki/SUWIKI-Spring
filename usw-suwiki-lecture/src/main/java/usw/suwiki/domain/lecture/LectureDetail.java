package usw.suwiki.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureDetail {
  public enum Evaluation {
    상대평가, 절대평가
  }

  @Column(name = "lecture_code")
  private String code;

  @Column(columnDefinition = "text") // todo: 추후에 수정할 것
  private double point;

  @Column(columnDefinition = "text") // todo: 추후에 수정할 것
  private String capprType;

  @Column
  private String diclNo;

  @Column
  private int grade;

  @Enumerated(EnumType.STRING)
  private Evaluation evaluateType;
}
