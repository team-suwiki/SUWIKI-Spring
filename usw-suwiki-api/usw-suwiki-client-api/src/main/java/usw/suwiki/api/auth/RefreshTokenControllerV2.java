package usw.suwiki.api.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/refreshtoken")
@RequiredArgsConstructor
public class RefreshTokenControllerV2 {
  private final UserBusinessService userBusinessService;

  @Statistics(target = USER)
  @PostMapping("/web-client/refresh")
  @ResponseStatus(OK)
  public ResponseForm clientTokenRefresh(
    @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
    HttpServletResponse response
  ) {
    Map<String, String> tokenPair = userBusinessService.executeJWTRefreshForWebClient(requestRefreshCookie);

    Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
    refreshCookie.setMaxAge(14 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return ResponseForm.success(new HashMap<>() {{
      put("AccessToken", tokenPair.get("AccessToken"));
    }});
  }

  @Statistics(target = USER)
  @PostMapping("/mobile-client/refresh")
  @ResponseStatus(OK)
  public ResponseForm tokenRefresh(@RequestHeader String Authorization) {
    return ResponseForm.success(userBusinessService.executeJWTRefreshForMobileClient(Authorization));
  }
}
