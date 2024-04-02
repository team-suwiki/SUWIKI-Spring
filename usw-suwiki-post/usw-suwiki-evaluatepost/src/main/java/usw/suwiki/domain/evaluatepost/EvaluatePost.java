package usw.suwiki.domain.evaluatepost;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluatePost extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Embedded
  private LectureInfo lectureInfo;

  @Embedded
  private LectureRating lectureRating;

  public EvaluatePost(Long userId, String content, LectureInfo lectureInfo, LectureRating lectureRating) {
    this.userId = userId;
    this.content = content;
    this.lectureInfo = lectureInfo;
    this.lectureRating = lectureRating;
  }

  public void update(String content, String lectureName, String selectedSemester, String professor, LectureRating lectureRating) {
    this.content = content;
    this.lectureInfo = lectureInfo.update(lectureName, selectedSemester, professor);
    this.lectureRating = lectureRating;
  }

  public void validateAuthor(Long userId) {
    if (!this.userId.equals(userId)) {
      throw new IllegalArgumentException("not an author"); // todo: 알맞는 예외 던지기
    }
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
}
