package usw.suwiki.domain.exampost;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseEntity;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPost extends BaseEntity {
  @Column(nullable = false)
  private Long userIdx;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Embedded
  private LectureInfo lectureInfo;

  @Embedded
  private ExamDetail examDetail;

  public void update(String content, String selectedSemester, ExamDetail examDetail) {
    this.content = content;
    this.lectureInfo.updateSemester(selectedSemester);
    this.examDetail = examDetail;
  }

  public Long getLectureId() {
    return lectureInfo.getLectureId();
  }

  public String getLectureName() {
    return lectureInfo.getLectureName();
  }

  public String getProfessor() {
    return lectureInfo.getProfessor();
  }

  public String getSelectedSemester() {
    return lectureInfo.getSelectedSemester();
  }

  public String getExamType() {
    return examDetail.getExamType();
  }

  public String getExamInfo() {
    return examDetail.getExamInfo();
  }

  public String getExamDifficulty() {
    return examDetail.getExamDifficulty();
  }
}
