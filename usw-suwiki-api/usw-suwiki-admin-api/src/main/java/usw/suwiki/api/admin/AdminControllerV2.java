package usw.suwiki.api.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.ExamPostReport;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.dto.UserAdminResponseDto;
import usw.suwiki.domain.user.service.AdminBusinessService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostBlacklistForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostNoProblemForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import static usw.suwiki.statistics.log.MonitorTarget.ADMIN;

@RestController
@RequestMapping("/v2/admin")
@RequiredArgsConstructor
public class AdminControllerV2 {
  private final AdminBusinessService adminBusinessService;

  @Statistics(target = ADMIN)
  @PostMapping("/login")
  @ResponseStatus(OK)
  public Map<String, String> administratorLogin(@Valid @RequestBody LoginForm loginForm) {
    return adminBusinessService.adminLogin(loginForm);
  }

  @Authorize(Role.ADMIN)
  @Statistics(target = ADMIN)
  @PostMapping("/evaluate-posts/restrict")
  @ResponseStatus(OK)
  public Map<String, Boolean> restrictEvaluatePost(
    @Valid @RequestBody EvaluatePostRestrictForm evaluatePostRestrictForm
  ) {
    return adminBusinessService.executeRestrictEvaluatePost(evaluatePostRestrictForm);
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(target = ADMIN)
  @PostMapping("/exam-post/restrict")
  public Map<String, Boolean> restrictExamPost(
    @Valid @RequestBody ExamPostRestrictForm examPostRestrictForm
  ) {
    return adminBusinessService.executeRestrictExamPost(examPostRestrictForm);
  }


  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(target = ADMIN)
  @PostMapping("/evaluate-post/blacklist")
  public Map<String, Boolean> banEvaluatePost(
    @Valid @RequestBody EvaluatePostBlacklistForm evaluatePostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListEvaluatePost(evaluatePostBlacklistForm);
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(target = ADMIN)
  @PostMapping("/exam-post/blacklist")
  public Map<String, Boolean> banExamPost(
    @Valid @RequestBody ExamPostBlacklistForm examPostBlacklistForm
  ) {
    return adminBusinessService.executeBlackListExamPost(examPostBlacklistForm);
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(target = ADMIN)
  @DeleteMapping("/evaluate-post")
  public Map<String, Boolean> noProblemEvaluatePost(
    @Valid @RequestBody EvaluatePostNoProblemForm evaluatePostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemEvaluatePost(evaluatePostNoProblemForm);
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(target = ADMIN)
  @DeleteMapping("/exam-post")
  public Map<String, Boolean> noProblemExamPost(
    @Valid @RequestBody ExamPostNoProblemForm examPostNoProblemForm
  ) {
    return adminBusinessService.executeNoProblemExamPost(examPostNoProblemForm);
  }

  @Authorize(Role.ADMIN)
  @Statistics(target = ADMIN)
  @GetMapping("/reported-posts")
  @ResponseStatus(OK)
  public UserAdminResponseDto.LoadAllReportedPostForm loadReportedPost() {
    return adminBusinessService.executeLoadAllReportedPosts();
  }

  @Authorize(Role.ADMIN)
  @Statistics(target = ADMIN)
  @GetMapping("/reported-evaluate/")
  @ResponseStatus(OK)
  public EvaluatePostReport loadDetailReportedEvaluatePost(@RequestParam Long target) {
    return adminBusinessService.executeLoadDetailReportedEvaluatePost(target);
  }

  @Authorize(Role.ADMIN)
  @Statistics(target = ADMIN)
  @GetMapping("/reported-exam/")
  @ResponseStatus(OK)
  public ExamPostReport loadDetailReportedExamPost(@RequestParam Long target) {
    return adminBusinessService.executeLoadDetailReportedExamPost(target);
  }
}
