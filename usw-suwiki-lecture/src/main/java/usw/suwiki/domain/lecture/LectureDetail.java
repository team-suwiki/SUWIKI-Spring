package usw.suwiki.domain.lecture;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureDetail {
  @Column(name = "lecture_code")
  private String code;

  @Column(columnDefinition = "text")
  private double point;

  @Column(columnDefinition = "text") // 추후에 수정할 것
  private String capprType;

  @Column
  private String diclNo;

  @Column
  private int grade;

  @Column
  private String evaluateType;
}
