package usw.suwiki.api.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;

@RestController
@RequestMapping("/v2/blacklist")
@RequiredArgsConstructor
public class BlacklistDomainControllerV2 {
  private final UserBusinessService userBusinessService;

  @ApiLogger(option = "user")
  @GetMapping("/logs")
  @ResponseStatus(OK)
  public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeLoadBlackListReason(Authorization);
  }
}
