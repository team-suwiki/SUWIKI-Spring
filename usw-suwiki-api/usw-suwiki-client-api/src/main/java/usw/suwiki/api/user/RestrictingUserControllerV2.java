package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.ApiLogger;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/v2/user/restricted-reason")
@RequiredArgsConstructor
public class RestrictingUserControllerV2 {
  private final UserBusinessService userBusinessService;

  @ApiLogger(option = "user")
  @ResponseStatus(OK)
  public ResponseForm loadRestrictedReason(@Valid @RequestHeader String Authorization) {
    return ResponseForm.success(userBusinessService.executeLoadRestrictedReason(Authorization));
  }
}
