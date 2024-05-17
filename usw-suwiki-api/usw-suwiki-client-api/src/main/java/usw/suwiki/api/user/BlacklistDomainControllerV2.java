package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/blacklist")
@RequiredArgsConstructor
public class BlacklistDomainControllerV2 {
  private final UserBusinessService userBusinessService;

  @Authorize
  @Statistics(target = USER)
  @GetMapping("/logs")
  @ResponseStatus(OK)
  public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(@Authenticated Long userId) {
    return userBusinessService.executeLoadBlackListReason(userId);
  }
}
