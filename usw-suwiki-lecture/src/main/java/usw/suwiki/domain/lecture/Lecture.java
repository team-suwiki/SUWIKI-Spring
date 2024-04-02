package usw.suwiki.domain.lecture;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.model.Evaluation;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseTimeEntity {
  private static final Pattern LECTURE_PATTERN = Pattern.compile("^(2\\d{3})-(1|2)$");

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "semester_list")
  private String semester;

  private String professor;

  @Column(name = "lecture_name")
  private String name;

  @Column(name = "major_type")
  private String majorType;

  @Column(name = "lecture_type")
  private String type;

  @Embedded
  private LectureEvaluationInfo lectureEvaluationInfo;

  @Embedded
  private LectureDetail lectureDetail;

  private int postsCount = 0;

  @Builder
  private Lecture(
    String semester,
    String professor,
    String name,
    String majorType,
    String type,
    LectureDetail lectureDetail
  ) {
    this.semester = semester;
    this.professor = professor;
    this.name = name;
    this.majorType = majorType;
    this.type = type;
    this.lectureDetail = lectureDetail;
    this.lectureEvaluationInfo = new LectureEvaluationInfo();
  }

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
}
