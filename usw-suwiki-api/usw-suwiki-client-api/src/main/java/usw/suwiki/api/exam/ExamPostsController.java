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
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.secure.TokenAgent;
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
  private final TokenAgent tokenAgent;

  @Monitoring(option = EXAM_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public ExamPostResponse.Details readAllExamPosts(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    return examPostService.loadAllExamPosts(userId, lectureId, new PageOption(page));
  }

  @Monitoring(option = EXAM_POSTS)
  @PostMapping("/purchase")
  @ResponseStatus(OK)
  public String purchaseExamPost(@RequestHeader String Authorization, @RequestParam Long lectureId) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
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
    tokenAgent.validateRestrictedUser(Authorization);
    Long userIdx = tokenAgent.parseId(Authorization);
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
    tokenAgent.validateRestrictedUser(Authorization);
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
    Long userIdx = tokenAgent.parseId(Authorization);
    return new ResponseForm(examPostService.loadAllMyExamPosts(new PageOption(page), userIdx));
  }

  @Monitoring(option = EXAM_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteExamPosts(@RequestHeader String Authorization, @RequestParam Long examIdx) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userIdx = tokenAgent.parseId(Authorization);
    examPostService.deleteExamPost(userIdx, examIdx);
    return "success";
  }

  @Monitoring(option = EXAM_POSTS)
  @GetMapping("/purchase")
  @ResponseStatus(OK)
  public ResponseForm readPurchaseHistoryApi(@RequestHeader String Authorization) {
    tokenAgent.validateJwt(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    return new ResponseForm(examPostService.loadPurchasedHistories(userId));
  }
}
