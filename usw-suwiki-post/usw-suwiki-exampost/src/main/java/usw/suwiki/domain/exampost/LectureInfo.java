package usw.suwiki.domain.exampost;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureInfo {
  @Column(nullable = false)
  private Long lectureId;
  private String lectureName; //과목
  private String selectedSemester;
  private String professor;   //교수

  public void updateSemester(String selectedSemester) {
    this.selectedSemester = selectedSemester;
  }
}
