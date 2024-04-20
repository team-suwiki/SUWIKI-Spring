package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.lecture.major.service.FavoriteMajorServiceV2;
import usw.suwiki.domain.user.dto.FavoriteSaveDto;
import usw.suwiki.statistics.annotation.Monitoring;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorOption.USER;

@RestController
@RequestMapping("/v2/favorite-major")
@RequiredArgsConstructor
public class FavoriteMajorController {
  private final FavoriteMajorServiceV2 favoriteMajorServiceV2;

  @Monitoring(option = USER)
  @PostMapping
  @ResponseStatus(OK)
  public String create(@RequestHeader String Authorization, @RequestBody FavoriteSaveDto favoriteSaveDto) {
    favoriteMajorServiceV2.save(Authorization, favoriteSaveDto.getMajorType());
    return "success";
  }

  @Monitoring(option = USER)
  @DeleteMapping
  @ResponseStatus(OK)
  public String delete(@RequestHeader String Authorization, @RequestParam String majorType) {
    favoriteMajorServiceV2.delete(Authorization, majorType);
    return "success";
  }

  @Monitoring(option = USER)
  @GetMapping
  @ResponseStatus(OK)
  public ResponseForm retrieve(@RequestHeader String Authorization) {
    return new ResponseForm(favoriteMajorServiceV2.findAllMajorTypeByUser(Authorization));
  }
}
