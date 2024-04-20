package usw.suwiki.api.exam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.JwtVerify;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.statistics.annotation.Monitoring;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorOption.EXAM_POSTS;

@RestController
@RequestMapping(value = "/exam-posts")
@RequiredArgsConstructor
public class ExamPostsController {
  private final ExamPostService examPostService;
  private final JwtAgent jwtAgent;

  @Monitoring(option = EXAM_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public ExamPostResponse.Details readAllExamPosts(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    validateAuth(Authorization);
    Long userId = jwtAgent.parseId(Authorization);
    return examPostService.loadAllExamPosts(userId, lectureId, new PageOption(page));
  }

  @Monitoring(option = EXAM_POSTS)
  @PostMapping("/purchase")
  @ResponseStatus(OK)
  public String purchaseExamPost(@RequestHeader String Authorization, @RequestParam Long lectureId) {
    validateAuth(Authorization);
    Long userId = jwtAgent.parseId(Authorization);
    examPostService.purchaseExamPost(userId, lectureId);
    return "success";
  }

  @Monitoring(option = EXAM_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public String writeExamPost(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @Valid @RequestBody ExamPostRequest.Create request
  ) {
    validateAuth(Authorization);
    Long userIdx = jwtAgent.parseId(Authorization);
    examPostService.write(userIdx, lectureId, request);
    return "success";
  }

  @Monitoring(option = EXAM_POSTS)
  @PutMapping
  public String updateExamPost(
    @RequestHeader String Authorization,
    @RequestParam Long examIdx,
    @Valid @RequestBody ExamPostRequest.Update request
  ) {
    jwtAgent.validateJwt(Authorization);

    if (jwtAgent.isRestrictedUser(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    examPostService.update(examIdx, request);
    return "success";
  }

  @JwtVerify
  @Monitoring(option = EXAM_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public ResponseForm findExamPostsByUserApi(
    @RequestHeader String Authorization,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    Long userIdx = jwtAgent.parseId(Authorization);
    return new ResponseForm(examPostService.loadAllMyExamPosts(new PageOption(page), userIdx));
  }

  @Monitoring(option = EXAM_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteExamPosts(@RequestHeader String Authorization, @RequestParam Long examIdx) {
    validateAuth(Authorization);
    Long userIdx = jwtAgent.parseId(Authorization);
    examPostService.deleteExamPost(userIdx, examIdx);
    return "success";
  }

  @Monitoring(option = EXAM_POSTS)
  @GetMapping("/purchase")
  @ResponseStatus(OK)
  public ResponseForm readPurchaseHistoryApi(@RequestHeader String Authorization) {
    jwtAgent.validateJwt(Authorization);
    Long userId = jwtAgent.parseId(Authorization);
    return new ResponseForm(examPostService.loadPurchasedHistories(userId));
  }

  private void validateAuth(String authorization) {
    jwtAgent.validateJwt(authorization);
    if (jwtAgent.isRestrictedUser(authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }
}
