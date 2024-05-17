package usw.suwiki.api.exam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;
import usw.suwiki.domain.exampost.service.ExamPostService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.EXAM_POSTS;

@RestController
@RequestMapping(value = "/exam-posts")
@RequiredArgsConstructor
public class ExamPostsController {
  private final ExamPostService examPostService;

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public ExamPostResponse.Details readAllExamPosts(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    return examPostService.loadAllExamPosts(userId, lectureId, new PageOption(page));
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @PostMapping("/purchase")
  @ResponseStatus(OK)
  public String purchaseExamPost(@Authenticated Long userId, @RequestParam Long lectureId) {
    examPostService.purchaseExamPost(userId, lectureId);
    return "success";
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public String writeExamPost(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @Valid @RequestBody ExamPostRequest.Create request
  ) {
    examPostService.write(userId, lectureId, request);
    return "success";
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @PutMapping
  @ResponseStatus(OK)
  public String updateExamPost(
    @RequestParam Long examIdx,
    @Valid @RequestBody ExamPostRequest.Update request
  ) {
    examPostService.update(examIdx, request); // todo: writer에 대한 검증
    return "success";
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public ResponseForm findExamPostsByUserApi(
    @Authenticated Long userId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    var response = examPostService.loadAllMyExamPosts(new PageOption(page), userId);
    return new ResponseForm(response);
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteExamPosts(@Authenticated Long userId, @RequestParam Long examIdx) {
    examPostService.deleteExamPost(userId, examIdx);
    return "success";
  }

  @Authorize
  @Statistics(target = EXAM_POSTS)
  @GetMapping("/purchase")
  @ResponseStatus(OK)
  public ResponseForm readPurchaseHistoryApi(@Authenticated Long userId) {
    var response = examPostService.loadPurchasedHistories(userId);
    return new ResponseForm(response);
  }
}
