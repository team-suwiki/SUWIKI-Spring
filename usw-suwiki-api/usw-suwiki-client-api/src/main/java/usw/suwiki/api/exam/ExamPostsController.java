package usw.suwiki.api.exam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.domain.viewexam.dto.ViewExamResponse;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.EXAM_POSTS;

@RestController
@RequestMapping(value = "/exam-posts")
@RequiredArgsConstructor
public class ExamPostsController {
  private final ExamPostService examPostService;

  @Authorize
  @Statistics(EXAM_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<ExamPostResponse.Details> getAllExamPosts(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    var response = examPostService.loadAllExamPosts(userId, lectureId, PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @GetMapping("/purchase")
  @ResponseStatus(OK)
  public CommonResponse<List<ViewExamResponse.PurchaseHistory>> getPurchaseHistories(@Authenticated Long userId) {
    var response = examPostService.loadPurchasedHistories(userId);
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public CommonResponse<List<ExamPostResponse.MyPost>> getWroteExamPosts(
    @Authenticated Long userId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    var response = examPostService.loadAllMyExamPosts(userId, PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @PostMapping("/purchase")
  @ResponseStatus(OK)
  public CommonResponse<?> purchaseExamPost(@Authenticated Long userId, @RequestParam Long lectureId) {
    examPostService.purchaseExamPost(userId, lectureId);
    return CommonResponse.success();
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public CommonResponse<?> writeExamPost(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @Valid @RequestBody ExamPostRequest.Create request
  ) {
    examPostService.write(userId, lectureId, request);
    return CommonResponse.success();
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @PutMapping
  @ResponseStatus(OK)
  public CommonResponse<?> updateExamPost(
    @Authenticated Long userId,
    @RequestParam Long examIdx,
    @Valid @RequestBody ExamPostRequest.Update request
  ) {
    examPostService.update(userId, examIdx, request);
    return CommonResponse.success();
  }

  @Authorize
  @Statistics(EXAM_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public CommonResponse<?> deleteExamPosts(@Authenticated Long userId, @RequestParam Long examIdx) {
    examPostService.deleteExamPost(userId, examIdx);
    return CommonResponse.success();
  }
}
