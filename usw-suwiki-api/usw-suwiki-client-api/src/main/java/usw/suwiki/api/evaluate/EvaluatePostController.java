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
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostRequest;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.evaluatepost.service.EvaluatePostService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/evaluate-posts")
@RequiredArgsConstructor
public class EvaluatePostController {
  private final EvaluatePostService evaluatePostService;
  private final JwtAgent jwtAgent;

  @ApiLogger(option = "evaluatePosts")
  @GetMapping
  @ResponseStatus(OK)
  public EvaluatePostResponse.Details readEvaluatePostsByLectureApi(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @RequestParam(required = false) Optional<Integer> page) {

    jwtAgent.validateJwt(Authorization);
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    Long userId = jwtAgent.getId(Authorization);
    return evaluatePostService.loadAllEvaluatePostsByLectureId(new PageOption(page), userId, lectureId);
  }

  @ApiLogger(option = "evaluatePosts")
  @PostMapping
  @ResponseStatus(OK)
  public String writeEvaluation(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId,
    @Valid @RequestBody EvaluatePostRequest.Create request
  ) {
    jwtAgent.validateJwt(Authorization);
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    Long userId = jwtAgent.getId(Authorization);
    evaluatePostService.write(userId, lectureId, request);

    return "success";
  }

  @ApiLogger(option = "evaluatePosts")
  @PutMapping
  @ResponseStatus(OK)
  public String updateEvaluation(
    @RequestHeader String Authorization,
    @RequestParam Long evaluateIdx,
    @Valid @RequestBody EvaluatePostRequest.Update request
  ) {
    jwtAgent.validateJwt(Authorization);
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    evaluatePostService.update(evaluateIdx, request);
    return "success";
  }

  @ApiLogger(option = "evaluatePosts")
  @GetMapping("/written")
  @ResponseStatus(OK)
  public ResponseForm findByUser(
    @RequestHeader String Authorization,
    @RequestParam(required = false) Optional<Integer> page
  ) {
    jwtAgent.validateJwt(Authorization);
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    Long userId = jwtAgent.getId(Authorization);
    return new ResponseForm(evaluatePostService.loadAllEvaluatePostsByUserId(new PageOption(page), userId));
  }

  @ApiLogger(option = "evaluatePosts")
  @DeleteMapping
  @ResponseStatus(OK)
  public String deleteEvaluation(@RequestParam Long evaluateIdx, @RequestHeader String Authorization) {
    jwtAgent.validateJwt(Authorization);
    if (jwtAgent.getUserIsRestricted(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    Long userId = jwtAgent.getId(Authorization);
    evaluatePostService.deleteEvaluatePost(evaluateIdx, userId);
    return "success";
  }
}
