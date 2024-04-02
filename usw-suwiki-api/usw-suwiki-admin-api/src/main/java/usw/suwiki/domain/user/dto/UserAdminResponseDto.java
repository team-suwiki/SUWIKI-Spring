package usw.suwiki.domain.user.dto;

import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.ExamPostReport;

import java.util.List;

public class UserAdminResponseDto {

  public record LoadAllReportedPostForm(
    List<ExamPostReport> examPostReports,
    List<EvaluatePostReport> evaluatePostReports
  ) {
  }
}
