package usw.suwiki.domain.exampost;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamDetail {
  private String examType;
  private String examInfo;    //시험방식
  private String examDifficulty;    //난이도
}
