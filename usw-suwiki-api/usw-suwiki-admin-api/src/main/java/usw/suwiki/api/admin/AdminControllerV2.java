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
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.report.EvaluatePostReport;
import usw.suwiki.domain.report.ExamPostReport;
import usw.suwiki.domain.user.Role;
import usw.suwiki.domain.user.dto.AdminRequest;
import usw.suwiki.domain.user.dto.AdminResponse;
import usw.suwiki.domain.user.dto.UserRequest;
import usw.suwiki.domain.user.service.AdminService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.ADMIN;

@RestController
@RequestMapping("/v2/admin")
@RequiredArgsConstructor
public class AdminControllerV2 {
  private final AdminService adminService;

  @Statistics(ADMIN)
  @PostMapping("/login")
  @ResponseStatus(OK)
  public CommonResponse<AdminResponse.Login> adminLogin(@Valid @RequestBody UserRequest.Login request) {
    return CommonResponse.ok(adminService.adminLogin(request.loginId(), request.password()));
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @PostMapping("/evaluate-posts/restrict")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> restrictEvaluatePost(@Valid @RequestBody AdminRequest.RestrictEvaluatePost request) {
    adminService.restrict(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(ADMIN)
  @PostMapping("/exam-post/restrict")
  public CommonResponse<Map<String, Boolean>> restrictExamPost(@Valid @RequestBody AdminRequest.RestrictExamPost request) {
    adminService.restrict(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(ADMIN)
  @PostMapping("/evaluate-post/blacklist")
  public CommonResponse<Map<String, Boolean>> banEvaluatePost(@Valid @RequestBody AdminRequest.EvaluatePostBlacklist request) {
    adminService.black(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(ADMIN)
  @PostMapping("/exam-post/blacklist")
  public CommonResponse<Map<String, Boolean>> banExamPost(@Valid @RequestBody AdminRequest.ExamPostBlacklist request) {
    adminService.black(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(ADMIN)
  @DeleteMapping("/evaluate-post")
  public CommonResponse<Map<String, Boolean>> noProblemEvaluatePost(@Valid @RequestBody AdminRequest.EvaluatePostNoProblem request) {
    adminService.dismissEvaluateReport(request.evaluateIdx());
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @ResponseStatus(OK)
  @Statistics(ADMIN)
  @DeleteMapping("/exam-post")
  public CommonResponse<Map<String, Boolean>> noProblemExamPost(@Valid @RequestBody AdminRequest.ExamPostNoProblem request) {
    adminService.dismissExamReport(request.examIdx());
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/reported-posts")
  @ResponseStatus(OK)
  public CommonResponse<AdminResponse.LoadAllReportedPost> loadReportedPost() {
    return CommonResponse.ok(adminService.loadAllReportedPosts());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/reported-evaluate/")
  @ResponseStatus(OK)
  public CommonResponse<EvaluatePostReport> loadDetailReportedEvaluatePost(@RequestParam Long target) {
    return CommonResponse.ok(adminService.loadDetailReportedEvaluatePost(target));
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/reported-exam/")
  @ResponseStatus(OK)
  public CommonResponse<ExamPostReport> loadDetailReportedExamPost(@RequestParam Long target) {
    return CommonResponse.ok(adminService.loadDetailReportedExamPost(target));
  }

  private Map<String, Boolean> success() { // legacy
    return new HashMap<>() {{
      put("Success", true);
    }};
  }
}
