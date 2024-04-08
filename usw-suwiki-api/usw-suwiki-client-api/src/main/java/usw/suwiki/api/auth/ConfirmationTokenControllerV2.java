package usw.suwiki.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.token.service.ConfirmationTokenBusinessService;
import usw.suwiki.statistics.annotation.ApiLogger;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v2/confirmation-token")
@RequiredArgsConstructor
public class ConfirmationTokenControllerV2 {
  private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

  @ApiLogger(option = "user")
  @GetMapping(value = "verify", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public String confirmEmail(@RequestParam("token") String token) {
    return confirmationTokenBusinessService.confirmToken(token);
  }
}
