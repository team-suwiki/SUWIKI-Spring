package usw.suwiki.api.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.domain.report.dto.ReportRequest;
import usw.suwiki.statistics.annotation.Monitoring;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.common.response.ApiResponseFactory.successFlag;
import static usw.suwiki.statistics.log.MonitorOption.USER;

@RestController
@RequestMapping("/user/report")
@RequiredArgsConstructor
public class ReportController {
  private final EvaluatePostService evaluatePostService;
  private final ExamPostService examPostService;
  private final TokenAgent tokenAgent;

  @Monitoring(option = USER)
  @PostMapping("/evaluate")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportEvaluate(
    @RequestHeader String Authorization,
    @Valid @RequestBody ReportRequest.Evaluate request
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long reportingUserId = tokenAgent.parseId(Authorization);
    evaluatePostService.report(reportingUserId, request.getEvaluateIdx());
    return successFlag();
  }

  @Monitoring(option = USER)
  @PostMapping("/exam")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportExam(
    @RequestHeader String Authorization,
    @Valid @RequestBody ReportRequest.Exam request
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long reportingUserId = tokenAgent.parseId(Authorization);
    examPostService.report(reportingUserId, request.getExamIdx());
    return successFlag();
  }
}
