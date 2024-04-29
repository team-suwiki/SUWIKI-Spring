package usw.suwiki.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.model.Evaluation;
import usw.suwiki.infra.jpa.BaseEntity;

import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseEntity {
  private static final Pattern LECTURE_PATTERN = Pattern.compile("^(2\\d{3})-(1|2)$");

  public enum Type {
    중핵, 전교, 기교, 선교, 소교, 전선, 전취, 전핵, 교직, RT, 전필, 선수, 기과, 전기
  }

  @Column(name = "semester_list")
  private String semester;

  private String professor;

  @Column(name = "lecture_name")
  private String name;

  @Column(name = "major_type")
  private String majorType;

  @Column(name = "lecture_type")
  @Enumerated(EnumType.STRING)
  private Type type;

  @Embedded
  private LectureDetail lectureDetail;

  @Embedded
  @Builder.Default
  private LectureEvaluationInfo lectureEvaluationInfo = new LectureEvaluationInfo();

  @Builder.Default
  private int postsCount = 0;

  public void evaluate(Evaluation evaluation) {
    this.lectureEvaluationInfo.apply(evaluation);
    this.lectureEvaluationInfo.calculateAverage(this.postsCount);
    this.postsCount += 1;
  }

  public void updateEvaluation(Evaluation current, Evaluation update) {
    this.lectureEvaluationInfo.cancel(current);
    this.lectureEvaluationInfo.apply(update);
    this.lectureEvaluationInfo.calculateAverage(this.postsCount);
  }

  public int getGrade() {
    return this.lectureDetail.getGrade();
  }

  @Deprecated
  public boolean isOld() {
    return this.semester.length() > 9;
  }

  public boolean isEquals(String name, String professor, String majorType, String diclNo) {
    return Objects.equals(this.name, name) &&
           Objects.equals(this.professor, professor) &&
           Objects.equals(this.majorType, majorType) &&
           Objects.equals(this.lectureDetail.getDiclNo(), diclNo);
  }

  public void addSemester(String singleSemester) {
    validateSingleSemester(singleSemester);
    if (this.semester.isEmpty() || this.semester.contains(singleSemester)) {
      return;
    }

    this.semester = this.semester + ", " + singleSemester;
  }

  public void removeSemester(String singleSemester) {
    validateSingleSemester(singleSemester);
    if (this.semester.contains(singleSemester)) {
      this.semester = this.semester.replace(", " + singleSemester, "");
    }
  }

  private void validateSingleSemester(String candidate) {
    if (!LECTURE_PATTERN.matcher(candidate).matches()) {
      throw new IllegalArgumentException("invalid semester");
    }
  }

  public String getType() {
    return type.name();
  }
}
