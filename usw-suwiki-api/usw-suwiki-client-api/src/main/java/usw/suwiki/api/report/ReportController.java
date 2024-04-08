package usw.suwiki.api.report;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.domain.report.dto.ReportRequest;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.common.response.ApiResponseFactory.successFlag;

@RestController
@RequestMapping("/user/report")
@RequiredArgsConstructor
public class ReportController {
  private final EvaluatePostService evaluatePostService;
  private final ExamPostService examPostService;
  private final JwtAgent jwtAgent;

  @ApiLogger(option = "user")
  @PostMapping("/evaluate")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportEvaluate(
    @RequestHeader String Authorization,
    @Valid @RequestBody ReportRequest.Evaluate request
  ) {
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
    Long reportingUserId = jwtAgent.getId(Authorization);
    evaluatePostService.report(reportingUserId, request.getEvaluateIdx());
    return successFlag();
  }

  @ApiLogger(option = "user")
  @PostMapping("/exam")
  @ResponseStatus(OK)
  public Map<String, Boolean> reportExam(
    @RequestHeader String Authorization,
    @Valid @RequestBody ReportRequest.Exam request
  ) {
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
    Long reportingUserId = jwtAgent.getId(Authorization);
    examPostService.report(reportingUserId, request.getExamIdx());
    return successFlag();
  }
}
