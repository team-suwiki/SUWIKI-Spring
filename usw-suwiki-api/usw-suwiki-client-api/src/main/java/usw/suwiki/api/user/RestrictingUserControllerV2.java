package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.auth.core.annotation.Login;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.Monitoring;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/user/restricted-reason")
@RequiredArgsConstructor
public class RestrictingUserControllerV2 {
  private final UserBusinessService userBusinessService;

  @Authorize
  @Monitoring(target = USER)
  @GetMapping
  @ResponseStatus(OK)
  public ResponseForm loadRestrictedReasons(@Login Long userId) {
    var response = userBusinessService.executeLoadRestrictedReason(userId);
    return ResponseForm.success(response);
  }
}
