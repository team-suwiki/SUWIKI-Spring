package usw.suwiki.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.user.service.ConfirmationTokenService;
import usw.suwiki.statistics.annotation.Statistics;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/confirmation-token")
@RequiredArgsConstructor
public class ConfirmationTokenControllerV2 {
  private final ConfirmationTokenService confirmationTokenService;

  @Statistics(USER)
  @GetMapping(value = "verify", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public CommonResponse<String> confirmEmail(@RequestParam("token") String token) {
    return CommonResponse.ok(confirmationTokenService.confirm(token));
  }
}
