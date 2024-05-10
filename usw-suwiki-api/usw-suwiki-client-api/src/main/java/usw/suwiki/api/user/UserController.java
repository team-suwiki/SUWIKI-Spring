package usw.suwiki.api.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.auth.core.annotation.Login;
import usw.suwiki.auth.token.service.ConfirmationTokenBusinessService;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.user.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.service.UserBusinessService;
import usw.suwiki.statistics.annotation.Monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckEmailForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.CheckLoginIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.EditMyPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindIdForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.FindPasswordForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.JoinForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.LoginForm;
import static usw.suwiki.domain.user.dto.UserRequestDto.UserQuitForm;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import static usw.suwiki.domain.user.dto.UserResponseDto.UserInformationResponseForm;
import static usw.suwiki.statistics.log.MonitorTarget.USER;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  private final UserBusinessService userBusinessService;
  private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

  @Monitoring(target = USER)
  @PostMapping("/check-id")
  @ResponseStatus(OK)
  public Map<String, Boolean> overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
    return userBusinessService.isDuplicatedId(checkLoginIdForm.loginId());
  }

  @Monitoring(target = USER)
  @PostMapping("/check-email")
  @ResponseStatus(OK)
  public Map<String, Boolean> overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
    return userBusinessService.isDuplicatedEmail(checkEmailForm.email());
  }

  @Monitoring(target = USER)
  @PostMapping("join")
  @ResponseStatus(OK)
  public Map<String, Boolean> join(@Valid @RequestBody JoinForm joinForm) {
    return userBusinessService.executeJoin(joinForm.loginId(), joinForm.password(), joinForm.email());
  }

  // todo: confirmationControllerV2와 같은 코드
  @Monitoring(target = USER)
  @GetMapping(value = "verify-email", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public String confirmEmail(@RequestParam("token") String token) {
    return confirmationTokenBusinessService.confirmToken(token);
  }

  @Monitoring(target = USER)
  @PostMapping("find-id")
  @ResponseStatus(OK)
  public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
    return userBusinessService.findId(findIdForm.email());
  }

  @Monitoring(target = USER)
  @PostMapping("find-pw")
  @ResponseStatus(OK)
  public Map<String, Boolean> findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
    return userBusinessService.findPw(findPasswordForm.loginId(), findPasswordForm.email());
  }

  @Authorize
  @Monitoring(target = USER)
  @PostMapping("reset-pw")
  @ResponseStatus(OK)
  public Map<String, Boolean> resetPw(@Login Long id, @Valid @RequestBody EditMyPasswordForm editMyPasswordForm) {
    return userBusinessService.editPassword(id, editMyPasswordForm.prePassword(), editMyPasswordForm.newPassword());
  }

  @Monitoring(target = USER)
  @PostMapping("login")
  @ResponseStatus(OK)
  public Map<String, String> mobileLogin(@Valid @RequestBody LoginForm loginForm) {
    return userBusinessService.login(loginForm.loginId(), loginForm.password());
  }

  @Monitoring(target = USER)
  @PostMapping("client-login")
  @ResponseStatus(OK)
  public Map<String, String> clientLogin(@Valid @RequestBody LoginForm loginForm, HttpServletResponse response) {
    Map<String, String> tokenPair = userBusinessService.login(loginForm.loginId(), loginForm.password());

    Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
    refreshCookie.setMaxAge(270 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return new HashMap<>() {{
      put("AccessToken", tokenPair.get("AccessToken"));
    }};
  }

  @Monitoring(target = USER)
  @PostMapping("client-logout")
  @ResponseStatus(OK)
  public Map<String, Boolean> clientLogout(HttpServletResponse response) {
    Cookie refreshCookie = new Cookie("refreshToken", "");
    refreshCookie.setMaxAge(0);
    response.addCookie(refreshCookie);

    return new HashMap<>() {{
      put("Success", true);
    }};
  }

  @Authorize
  @Monitoring(target = USER)
  @GetMapping("/my-page")
  @ResponseStatus(OK)
  public UserInformationResponseForm myPage(@Login Long userId) {
    return userBusinessService.loadMyPage(userId);
  }

  @Monitoring(target = USER)
  @PostMapping("/client-refresh")
  @ResponseStatus(OK)
  public Map<String, String> clientTokenRefresh(
    @CookieValue(value = "refreshToken") Cookie requestRefreshCookie,
    HttpServletResponse response
  ) {
    Map<String, String> tokenPair = userBusinessService.executeJWTRefreshForWebClient(requestRefreshCookie);

    Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
    refreshCookie.setMaxAge(14 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return new HashMap<>() {{
      put("AccessToken", tokenPair.get("AccessToken"));
    }};
  }

  @Monitoring(target = USER)
  @PostMapping("/refresh")
  @ResponseStatus(OK)
  public Map<String, String> tokenRefresh(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeJWTRefreshForMobileClient(Authorization); // todo: check business logic
  }

  @Monitoring(target = USER)
  @PostMapping("quit")
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public Map<String, Boolean> userQuit(
    @Valid @RequestBody UserQuitForm userQuitForm,
    @Valid @RequestHeader String Authorization
  ) {
//        return userBusinessService.executeQuit(
//                Authorization,
//                userQuitForm.password()
//        );

    // TODO fix: 회원 탈퇴 오류에 의한 임시 처리. 유저 탈퇴 로직 fix 후 복구해야 합니다.
    throw new AccountException(ExceptionType.SERVER_ERROR);
  }

  @Authorize
  @Monitoring(target = USER)
  @PostMapping("/favorite-major")
  @ResponseStatus(OK)
  public String saveFavoriteMajor(@Login Long userId, @Valid @RequestBody FavoriteSaveDto favoriteSaveDto) {
    userBusinessService.saveFavoriteMajor(userId, favoriteSaveDto);
    return "success";
  }

  @Authorize
  @Monitoring(target = USER)
  @DeleteMapping("/favorite-major")
  @ResponseStatus(OK)
  public String deleteFavoriteMajor(@Login Long userId, @RequestParam String majorType) {
    userBusinessService.deleteFavoriteMajor(userId, majorType);
    return "success";
  }

  @Authorize
  @Monitoring(target = USER)
  @GetMapping("/favorite-major")
  @ResponseStatus(OK)
  public ResponseForm loadFavoriteMajors(@Login Long userId) {
    var response = userBusinessService.executeFavoriteMajorLoad(userId);
    return new ResponseForm(response);
  }

  @Monitoring(target = USER)
  @GetMapping(value = "/suki", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public String thanksToSuwiki() {
    return """
        <center>\uD83D\uDE00 Thank You Suki! \uD83D\uDE00 <br><br> You gave to me a lot of knowledge <br><br>
        He is my Tech-Mentor <br><br>
        If you wanna contact him <br><br>
        <a href = https://github.com/0xsuky>
        <b>https://github.com/0xsuky<b>
        </center>
      """;
  }

  @Authorize
  @Monitoring(target = USER)
  @GetMapping("/restricted-reason")
  @ResponseStatus(OK) // todo: RestrictingUserControllerV2 동일한 API
  public List<LoadMyRestrictedReasonResponseForm> loadRestrictedReason(@Login Long userId) {
    return userBusinessService.executeLoadRestrictedReason(userId);
  }

  @Authorize
  @Monitoring(target = USER)
  @GetMapping("/blacklist-reason")
  @ResponseStatus(OK) // todo: BlacklistDomainControllerV2 동일한 API
  public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(@Login Long userId) {
    return userBusinessService.executeLoadBlackListReason(userId);
  }
}
