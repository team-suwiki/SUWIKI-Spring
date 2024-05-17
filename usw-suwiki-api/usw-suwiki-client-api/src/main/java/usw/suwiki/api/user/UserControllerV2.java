package usw.suwiki.api.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckEmailForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckLoginIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.UserQuitForm;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
public class UserControllerV2 {
  private final UserBusinessService userBusinessService;

  @Statistics(target = USER)
  @PostMapping("/loginId/check")
  @ResponseStatus(OK)
  public ResponseForm overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
    var response = userBusinessService.isDuplicatedId(checkLoginIdForm.loginId());
    return ResponseForm.success(response);
  }

  @Statistics(target = USER)
  @PostMapping("/email/check")
  @ResponseStatus(OK)
  public ResponseForm overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
    var response = userBusinessService.isDuplicatedEmail(checkEmailForm.email());
    return ResponseForm.success(response);
  }

  @Statistics(target = USER)
  @PostMapping
  @ResponseStatus(OK)
  public ResponseForm join(@Valid @RequestBody JoinForm joinForm) {
    Map<?, ?> response = userBusinessService.executeJoin(joinForm.loginId(), joinForm.password(), joinForm.email());
    return ResponseForm.success(response);
  }

  @Statistics(target = USER)
  @PostMapping("inquiry-loginId")
  @ResponseStatus(OK)
  public ResponseForm findId(@Valid @RequestBody FindIdForm findIdForm) {
    var response = userBusinessService.findId(findIdForm.email());
    return ResponseForm.success(response);
  }

  @Statistics(target = USER)
  @PostMapping("inquiry-password")
  @ResponseStatus(OK)
  public ResponseForm findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
    var response = userBusinessService.findPw(findPasswordForm.loginId(), findPasswordForm.email());
    return ResponseForm.success(response);
  }

  @Authorize
  @Statistics(target = USER)
  @PatchMapping("password")
  @ResponseStatus(OK)
  public ResponseForm resetPw(@Authenticated Long userId, @Valid @RequestBody EditMyPasswordForm request) {
    var response = userBusinessService.editPassword(userId, request.prePassword(), request.newPassword());
    return ResponseForm.success(response);
  }

  @Statistics(target = USER)
  @PostMapping("mobile-login")
  @ResponseStatus(OK)
  public ResponseForm mobileLogin(@Valid @RequestBody LoginForm request) {
    return ResponseForm.success(userBusinessService.login(request.loginId(), request.password()));
  }

  @Statistics(target = USER)
  @PostMapping("web-login")
  @ResponseStatus(OK)
  public ResponseForm webLogin(
    @Valid @RequestBody LoginForm loginForm,
    HttpServletResponse response
  ) {
    Map<String, String> tokenPair = userBusinessService.login(loginForm.loginId(), loginForm.password());

    Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
    refreshCookie.setMaxAge(270 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return ResponseForm.success(new HashMap<>() {{
      put("AccessToken", tokenPair.get("AccessToken"));
    }});
  }

  @Authorize
  @Statistics(target = USER)
  @PostMapping("client-logout")
  @ResponseStatus(OK)
  public ResponseForm clientLogout(HttpServletResponse response) {
    Cookie refreshCookie = new Cookie("refreshToken", "");
    refreshCookie.setMaxAge(0);
    response.addCookie(refreshCookie);

    return ResponseForm.success(new HashMap<>() {{
      put("Success", true);
    }});
  }

  @Authorize
  @Statistics(target = USER)
  @GetMapping
  @ResponseStatus(OK)
  public ResponseForm myPage(@Authenticated Long userId) {
    var response = userBusinessService.loadMyPage(userId);
    return ResponseForm.success(response);
  }

  @Authorize
  @Statistics(target = USER)
  @DeleteMapping
  @ResponseStatus(OK)
  public ResponseForm userQuit(@Authenticated Long userId, @Valid @RequestBody UserQuitForm userQuitForm) {
    return ResponseForm.success(userBusinessService.quit(userId, userQuitForm.password()));
  }
}
