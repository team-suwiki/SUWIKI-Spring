package usw.suwiki.api.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService adminService;

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/report/list")
  @ResponseStatus(OK)
  public CommonResponse<AdminResponse.LoadAllReportedPost> loadReportedPost() {
    return CommonResponse.ok(adminService.loadAllReportedPosts());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/report/evaluate/")
  @ResponseStatus(OK)  // todo: domain dependency
  public CommonResponse<EvaluatePostReport> loadDetailReportedEvaluatePost(@RequestParam Long target) {
    return CommonResponse.ok(adminService.loadDetailReportedEvaluatePost(target));
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @GetMapping("/report/exam/")
  @ResponseStatus(OK)
  public CommonResponse<ExamPostReport> loadDetailReportedExamPost(@RequestParam Long target) {
    return CommonResponse.ok(adminService.loadDetailReportedExamPost(target));
  }

  @Statistics(ADMIN)
  @PostMapping("/login")
  @ResponseStatus(OK)
  public CommonResponse<AdminResponse.Login> adminLogin(@Valid @RequestBody UserRequest.Login request) {
    return CommonResponse.ok(adminService.adminLogin(request.loginId(), request.password()));
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @PostMapping("/restrict/evaluate-posts")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> restrictEvaluatePost(@Valid @RequestBody AdminRequest.RestrictEvaluatePost request) {
    adminService.restrict(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @PostMapping("/restrict/exam-post")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> restrictExamPost(@Valid @RequestBody AdminRequest.RestrictExamPost request) {
    adminService.restrict(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @PostMapping("/blacklist/evaluate-post")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> banEvaluatePost(@Valid @RequestBody AdminRequest.EvaluatePostBlacklist request) {
    adminService.black(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @PostMapping("/blacklist/exam-post")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> banExamPost(@Valid @RequestBody AdminRequest.ExamPostBlacklist request) {
    adminService.black(request);
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @DeleteMapping("/no-problem/evaluate-post")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> noProblemEvaluatePost(@Valid @RequestBody AdminRequest.EvaluatePostNoProblem request) {
    adminService.dismissEvaluateReport(request.evaluateIdx());
    return CommonResponse.ok(success());
  }

  @Authorize(Role.ADMIN)
  @Statistics(ADMIN)
  @DeleteMapping("/no-problem/exam-post")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> noProblemExamPost(@Valid @RequestBody AdminRequest.ExamPostNoProblem request) {
    adminService.dismissExamReport(request.examIdx());
    return CommonResponse.ok(success());
  }

  private Map<String, Boolean> success() { // legacy
    return new HashMap<>() {{
      put("Success", true);
    }};
  }
}
