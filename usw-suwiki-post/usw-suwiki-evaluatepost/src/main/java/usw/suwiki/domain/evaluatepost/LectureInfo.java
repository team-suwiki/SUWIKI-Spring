package usw.suwiki.domain.evaluatepost;

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

  public LectureInfo update(String lectureName, String selectedSemester, String professor) {
    return new LectureInfo(this.lectureId, lectureName, selectedSemester, professor);
  }
}
