package usw.suwiki.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.token.service.ConfirmationTokenBusinessService;
import usw.suwiki.statistics.annotation.Monitoring;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorOption.USER;

@RestController
@RequestMapping("/v2/confirmation-token")
@RequiredArgsConstructor
public class ConfirmationTokenControllerV2 {
  private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

  @Monitoring(option = USER)
  @GetMapping(value = "verify", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public String confirmEmail(@RequestParam("token") String token) {
    return confirmationTokenBusinessService.confirmToken(token);
  }
}
