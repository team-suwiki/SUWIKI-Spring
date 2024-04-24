package usw.suwiki.api.evaluate;

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
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.statistics.annotation.Monitoring;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorOption.EVALUATE_POSTS;

@RestController
@RequestMapping(value = "/evaluate-posts")
@RequiredArgsConstructor
public class EvaluatePostController {
  private final EvaluatePostService evaluatePostService;
  private final TokenAgent tokenAgent;

  @Monitoring(option = EVALUATE_POSTS)
  @GetMapping
  @ResponseStatus(OK)
  public EvaluatePostResponse.Details readEvaluatePostsByLectureApi(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    return evaluatePostService.loadAllEvaluatePostsByLectureId(new PageOption(page), userId, lectureId);
  }

  @Monitoring(option = EVALUATE_POSTS)
  @PostMapping
  @ResponseStatus(OK)
  public String writeEvaluation(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @Valid @RequestBody EvaluatePostRequest.Create request
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    evaluatePostService.write(userId, lectureId, request);

    return "success";
  }

  @Monitoring(option = EVALUATE_POSTS)
  @PutMapping
  @ResponseStatus(OK)
  public String updateEvaluation(
    @RequestHeader String Authorization,
    @RequestParam Long evaluateIdx,
    @Valid @RequestBody EvaluatePostRequest.Update request
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    evaluatePostService.update(evaluateIdx, request);
    return "success";
  }

  @Monitoring(option = EVALUATE_POSTS)
  @GetMapping("/written")
  @ResponseStatus(OK)
  public ResponseForm findByUser(
    @RequestHeader String Authorization,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    return new ResponseForm(evaluatePostService.loadAllEvaluatePostsByUserId(new PageOption(page), userId));
  }

  @Monitoring(option = EVALUATE_POSTS)
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteEvaluation(@RequestParam Long evaluateIdx, @RequestHeader String Authorization) {
    tokenAgent.validateRestrictedUser(Authorization);
    Long userId = tokenAgent.parseId(Authorization);
    evaluatePostService.deleteEvaluatePost(evaluateIdx, userId);
    return "success";
  }
}
