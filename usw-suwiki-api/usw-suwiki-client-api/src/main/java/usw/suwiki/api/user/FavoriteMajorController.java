package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.lecture.major.service.FavoriteMajorServiceV2;
import usw.suwiki.domain.user.dto.FavoriteSaveDto;
import usw.suwiki.statistics.annotation.Statistics;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
public class FavoriteMajorController {
  private final FavoriteMajorServiceV2 favoriteMajorServiceV2;

  @Authorize
  @Statistics(target = USER)
  @GetMapping
  @ResponseStatus(OK)
  public ResponseForm getFavoriteMajors(@Authenticated Long userId) {
    var response = favoriteMajorServiceV2.findAllMajorTypeByUser(userId);
    return new ResponseForm(response);
  }

  @Authorize
  @Statistics(target = USER)
  @PostMapping
  @ResponseStatus(OK)
  public String create(@Authenticated Long userId, @RequestBody FavoriteSaveDto favoriteSaveDto) {
    favoriteMajorServiceV2.save(userId, favoriteSaveDto.getMajorType());
    return "success";
  }

  @Authorize
  @Statistics(target = USER)
  @DeleteMapping
  @ResponseStatus(OK)
  public String delete(@Authenticated Long userId, @RequestParam String majorType) {
    favoriteMajorServiceV2.delete(userId, majorType);
    return "success";
  }
}
