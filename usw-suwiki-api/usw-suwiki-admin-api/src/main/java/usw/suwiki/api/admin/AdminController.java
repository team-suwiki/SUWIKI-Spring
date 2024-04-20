package usw.suwiki.api.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.JwtVerify;
import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.ExamPostReport;
import usw.suwiki.domain.user.dto.UserAdminResponseDto;
import usw.suwiki.domain.user.service.AdminBusinessService;
import usw.suwiki.statistics.annotation.Monitoring;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import static usw.suwiki.statistics.log.MonitorOption.ADMIN;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminBusinessService adminBusinessService;

  @Monitoring(option = ADMIN)
  @PostMapping("/login")
  @ResponseStatus(OK)
  public Map<String, String> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
    return adminBusinessService.executeAdminLogin(loginForm);
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @PostMapping("/restrict/evaluate-posts")
  @ResponseStatus(OK)
  public Map<String, Boolean> restrictEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
  ) {
    return adminBusinessService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @PostMapping("/restrict/exam-post")
  @ResponseStatus(OK)
  public Map<String, Boolean> restrictExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
  ) {
    return adminBusinessService.executeRestrictExamPost(examPostRestrictForm);
  }


  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @PostMapping("/blacklist/evaluate-post")
  @ResponseStatus(OK)
  public Map<String, Boolean> banEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @PostMapping("/blacklist/exam-post")
  @ResponseStatus(OK)
  public Map<String, Boolean> banExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListExamPost(examPostBlacklistForm);
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @DeleteMapping("/no-problem/evaluate-post")
  @ResponseStatus(OK)
  public Map<String, Boolean> noProblemEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemEvaluatePost(evaluatePostNoProblemForm);
  }

  @JwtVerify(option = "ADMIN") // todo: 통일
  @Monitoring(option = ADMIN)
  @DeleteMapping("/no-problem/exam-post")
  @ResponseStatus(OK)
  public Map<String, Boolean> noProblemExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemExamPost(examPostNoProblemForm);
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @GetMapping("/report/list")
  @ResponseStatus(OK)
  public UserAdminResponseDto.LoadAllReportedPostForm loadReportedPost(@RequestHeader String Authorization) {
    return adminBusinessService.executeLoadAllReportedPosts();
  }

  @JwtVerify(option = "ADMIN")
  @Monitoring(option = ADMIN)
  @GetMapping("/report/evaluate/")
  @ResponseStatus(OK)  // todo: domain dependency
  public EvaluatePostReport loadDetailReportedEvaluatePost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestParam Long target
  ) {
    return adminBusinessService.executeLoadDetailReportedEvaluatePost(target);
  }

  @Monitoring(option = ADMIN)
  @GetMapping("/report/exam/")
  @ResponseStatus(OK)
  public ExamPostReport loadDetailReportedExamPost(
    @Valid @RequestHeader String Authorization,
    @Valid @RequestParam Long target
  ) {
    return adminBusinessService.executeLoadDetailReportedExamPost(target);
  }
}
