package usw.suwiki.domain.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.report.model.Report;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExamPostReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long examIdx;

  @Column
  private Long reportedUserIdx;

  @Column
  private Long reportingUserIdx;

  @Column
  private String professor;

  @Column
  private String lectureName;

  @Column
  private String content;

  @Column
  private LocalDateTime reportedDate;

  private ExamPostReport(Long examIdx, Long reportedUserIdx, Long reportingUserIdx, String professor, String lectureName, String content, LocalDateTime reportedDate) {
    this.examIdx = examIdx;
    this.reportedUserIdx = reportedUserIdx;
    this.reportingUserIdx = reportingUserIdx;
    this.professor = professor;
    this.lectureName = lectureName;
    this.content = content;
    this.reportedDate = reportedDate;
  }

  public static ExamPostReport from(Report report) {
    return new ExamPostReport(
      report.targetId(),
      report.reportedUserId(),
      report.reportingUserId(),
      report.professor(),
      report.lecture(),
      report.content(),
      LocalDateTime.now()
    );
  }
}
