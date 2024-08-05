package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.user.dto.UserResponse;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/user/restricted-reason")
@RequiredArgsConstructor
public class RestrictingUserControllerV2 {
  private final UserService userService;

  @Authorize
  @Statistics(USER)
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<List<UserResponse.RestrictedReason>> loadRestrictedReasons(@Authenticated Long userId) {
    var response = userService.loadRestrictedReason(userId);
    return CommonResponse.ok(response);
  }
}
