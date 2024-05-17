package usw.suwiki.api.evaluate;

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
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.EVALUATE_POSTS;

@RestController
@RequestMapping(value = "/evaluate-posts")
@RequiredArgsConstructor
public class EvaluatePostController {
  private final EvaluatePostService evaluatePostService;

  @Authorize
  @Statistics(target = EVALUATE_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public EvaluatePostResponse.Details readEvaluatePostsByLectureApi(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    return evaluatePostService.loadAllEvaluatePostsByLectureId(new PageOption(page), userId, lectureId);
  }

  @Authorize
  @Statistics(target = EVALUATE_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public String writeEvaluation(
    @Authenticated Long userId,
    @RequestParam Long lectureId,
    @Valid @RequestBody EvaluatePostRequest.Create request
  ) {
    evaluatePostService.write(userId, lectureId, request);
    return "success";
  }

  @Authorize
  @Statistics(target = EVALUATE_POSTS)
  @PutMapping
  @ResponseStatus(OK)
  public String updateEvaluation(
    @RequestParam Long evaluateIdx,
    @Valid @RequestBody EvaluatePostRequest.Update request
  ) {
    evaluatePostService.update(evaluateIdx, request); // todo: writer에 대한 검증
    return "success";
  }

  @Authorize
  @Statistics(target = EVALUATE_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public ResponseForm findByUser(
    @Authenticated Long userId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    var response = evaluatePostService.loadAllEvaluatePostsByUserId(new PageOption(page), userId);
    return new ResponseForm(response);
  }

  @Authorize
  @Statistics(target = EVALUATE_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteEvaluation(@Authenticated Long userId, @RequestParam Long evaluateIdx) {
    evaluatePostService.deleteEvaluatePost(evaluateIdx, userId);
    return "success";
  }
}
