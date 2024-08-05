package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.lecture.major.service.FavoriteMajorServiceV2;
import usw.suwiki.domain.user.dto.MajorRequest;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
public class FavoriteMajorControllerV2 {
  private final FavoriteMajorServiceV2 favoriteMajorServiceV2;

  @Authorize
  @Statistics(USER)
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<List<String>> getFavoriteMajors(@Authenticated Long userId) {
    var response = favoriteMajorServiceV2.findAllMajorTypeByUser(userId);
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(USER)
  @PostMapping
  @ResponseStatus(OK)
  public CommonResponse<String> create(@Authenticated Long userId, @RequestBody MajorRequest majorRequest) {
    favoriteMajorServiceV2.save(userId, majorRequest.getMajorType());
    return CommonResponse.ok("success");
  }

  @Authorize
  @Statistics(USER)
  @DeleteMapping
  @ResponseStatus(OK)
  public CommonResponse<String> delete(@Authenticated Long userId, @RequestParam String majorType) {
    favoriteMajorServiceV2.delete(userId, majorType);
    return CommonResponse.ok("success");
  }
}
