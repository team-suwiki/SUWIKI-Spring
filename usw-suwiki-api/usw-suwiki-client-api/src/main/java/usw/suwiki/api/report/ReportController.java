package usw.suwiki.api.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.domain.report.dto.ReportRequest;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.common.response.ApiResponseFactory.successFlag;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/user/report")
@RequiredArgsConstructor
public class ReportController {
  private final EvaluatePostService evaluatePostService;
  private final ExamPostService examPostService;

  @Authorize
  @Statistics(target = USER)
  @PostMapping("/evaluate")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportEvaluate(
    @Authenticated Long reportingUserId,
    @Valid @RequestBody ReportRequest.Evaluate request
  ) {
    evaluatePostService.report(reportingUserId, request.getEvaluateIdx());
    return successFlag();
  }

  @Authorize
  @Statistics(target = USER)
  @PostMapping("/exam")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportExam(
    @Authenticated Long reportingUserId,
    @Valid @RequestBody ReportRequest.Exam request
  ) {
    examPostService.report(reportingUserId, request.getExamIdx());
    return successFlag();
  }
}
