package usw.suwiki.api.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.core.secure.model.Tokens;
import usw.suwiki.domain.user.dto.UserRequest;
import usw.suwiki.domain.user.dto.UserResponse;
import usw.suwiki.domain.user.service.AuthService;
import usw.suwiki.domain.user.service.UserService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
public class UserControllerV2 {
  private final UserService userService;
  private final AuthService authService;

  @Statistics(USER)
  @PostMapping("/loginId/check")
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Overlap> isOverlapId(@Valid @RequestBody UserRequest.CheckLoginId request) {
    var isDuplicated = authService.isDuplicatedId(request.loginId());
    return CommonResponse.ok(new UserResponse.Overlap(isDuplicated));
  }

  @Statistics(USER)
  @PostMapping("/email/check")
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Overlap> isOverlapEmail(@Valid @RequestBody UserRequest.CheckEmail request) {
    var isDuplicated = authService.isDuplicatedEmail(request.email());
    return CommonResponse.ok(new UserResponse.Overlap(isDuplicated));
  }

  @Statistics(USER)
  @PostMapping
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Success> join(@Valid @RequestBody UserRequest.Join join) {
    authService.join(join.loginId(), join.password(), join.email());
    return CommonResponse.ok(new UserResponse.Success(true));
  }

  @Statistics(USER)
  @PostMapping("inquiry-loginId")
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Success> findId(@Valid @RequestBody UserRequest.FindId findId) {
    authService.findId(findId.email());
    return CommonResponse.ok(new UserResponse.Success(true));
  }

  @Statistics(USER)
  @PostMapping("inquiry-password")
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Success> findPw(@Valid @RequestBody UserRequest.FindPassword findPassword) {
    authService.findPw(findPassword.loginId(), findPassword.email());
    return CommonResponse.ok(new UserResponse.Success(true));
  }

  @Authorize
  @Statistics(USER)
  @PatchMapping("password")
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Success> resetPw(@Authenticated Long userId, @Valid @RequestBody UserRequest.EditPassword request) {
    userService.changePassword(userId, request.prePassword(), request.newPassword());
    return CommonResponse.ok(new UserResponse.Success(true));
  }

  @Statistics(USER)
  @PostMapping("mobile-login")
  @ResponseStatus(OK)
  public CommonResponse<Tokens> mobileLogin(@Valid @RequestBody UserRequest.Login request) {
    return CommonResponse.ok(authService.login(request.loginId(), request.password()));
  }

  @Statistics(USER)
  @PostMapping("web-login")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, String>> webLogin(
    @Valid @RequestBody UserRequest.Login login,
    HttpServletResponse response
  ) {
    var token = authService.login(login.loginId(), login.password());

    Cookie refreshCookie = new Cookie("refreshToken", token.getRefreshToken());
    refreshCookie.setMaxAge(270 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return CommonResponse.ok(new HashMap<>() {{
      put("AccessToken", token.getAccessToken());
    }});
  }

  @Authorize
  @Statistics(USER)
  @PostMapping("client-logout")
  @ResponseStatus(OK)
  public CommonResponse<Map<String, Boolean>> clientLogout(HttpServletResponse response) {
    Cookie refreshCookie = new Cookie("refreshToken", "");
    refreshCookie.setMaxAge(0);
    response.addCookie(refreshCookie);

    return CommonResponse.ok(new HashMap<>() {{
      put("Success", true);
    }});
  }

  @Authorize
  @Statistics(USER)
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.MyPage> myPage(@Authenticated Long userId) {
    var response = userService.loadMyPage(userId);
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(USER)
  @DeleteMapping
  @ResponseStatus(OK)
  public CommonResponse<UserResponse.Success> quit(@Authenticated Long userId, @Valid @RequestBody UserRequest.Quit quit) {
    userService.quit(userId, quit.password());
    return CommonResponse.ok(new UserResponse.Success(true));
  }
}
