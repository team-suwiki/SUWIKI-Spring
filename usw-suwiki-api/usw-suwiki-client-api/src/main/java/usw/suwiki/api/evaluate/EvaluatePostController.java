package usw.suwiki.api.evaluate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.EVALUATE_POSTS;

@RestController
@RequestMapping(value = "/evaluate-posts")
@RequiredArgsConstructor
public class EvaluatePostController {
  private final EvaluatePostService evaluatePostService;

  @Authorize
  @Statistics(EVALUATE_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<EvaluatePostResponse.Details> getAllOfLecture(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    var response = evaluatePostService.loadAllEvaluatePostsByLectureId(userId, lectureId, PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(EVALUATE_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public CommonResponse<?> writeEvaluation(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @Valid @RequestBody EvaluatePostRequest.Create request
  ) {
    evaluatePostService.write(userId, lectureId, request);
    return CommonResponse.success();
  }

  @Authorize
  @Statistics(EVALUATE_POSTS)
  @PutMapping
  @ResponseStatus(OK)
  public CommonResponse<?> updateEvaluation(
    @Authenticated Long userId,
    @RequestParam Long evaluateIdx,
    @Valid @RequestBody EvaluatePostRequest.Update request
  ) {
    evaluatePostService.update(userId, evaluateIdx, request);
    return CommonResponse.success();
  }

  @Authorize
  @Statistics(EVALUATE_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public CommonResponse<List<EvaluatePostResponse.MyPost>> findByUser(@Authenticated Long userId, @RequestParam(required = false) Optional<Integer> page) {
    var response = evaluatePostService.loadAllEvaluatePostsByUserId(userId, PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(EVALUATE_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public CommonResponse<?> deleteEvaluation(@Authenticated Long userId, @RequestParam Long evaluateIdx) {
    evaluatePostService.erase(userId, evaluateIdx);
    return CommonResponse.success();
  }
}
