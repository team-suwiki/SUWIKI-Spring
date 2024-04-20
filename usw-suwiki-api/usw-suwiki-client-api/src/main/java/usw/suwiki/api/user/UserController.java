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
import static usw.suwiki.statistics.log.MonitorOption.USER;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  private final UserBusinessService userBusinessService;
  private final ConfirmationTokenBusinessService confirmationTokenBusinessService;

  @Monitoring(option = USER)
  @PostMapping("/check-id")
  @ResponseStatus(OK)
  public Map<String, Boolean> overlapId(@Valid @RequestBody CheckLoginIdForm checkLoginIdForm) {
    return userBusinessService.executeCheckId(checkLoginIdForm.loginId());
  }

  @Monitoring(option = USER)
  @PostMapping("/check-email")
  @ResponseStatus(OK)
  public Map<String, Boolean> overlapEmail(@Valid @RequestBody CheckEmailForm checkEmailForm) {
    return userBusinessService.executeCheckEmail(checkEmailForm.email());
  }

  @Monitoring(option = USER)
  @PostMapping("join")
  @ResponseStatus(OK)
  public Map<String, Boolean> join(@Valid @RequestBody JoinForm joinForm) {
    return userBusinessService.executeJoin(joinForm.loginId(), joinForm.password(), joinForm.email());
  }

  // todo: confirmationControllerV2와 같은 코드
  @Monitoring(option = USER)
  @GetMapping(value = "verify-email", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
  @ResponseStatus(OK)
  public String confirmEmail(@RequestParam("token") String token) {
    return confirmationTokenBusinessService.confirmToken(token);
  }

  @Monitoring(option = USER)
  @PostMapping("find-id")
  @ResponseStatus(OK)
  public Map<String, Boolean> findId(@Valid @RequestBody FindIdForm findIdForm) {
    return userBusinessService.executeFindId(findIdForm.email());
  }

  @Monitoring(option = USER)
  @PostMapping("find-pw")
  @ResponseStatus(OK)
  public Map<String, Boolean> findPw(@Valid @RequestBody FindPasswordForm findPasswordForm) {
    return userBusinessService.executeFindPw(findPasswordForm.loginId(), findPasswordForm.email());
  }

  @Monitoring(option = USER)
  @PostMapping("reset-pw")
  @ResponseStatus(OK)
  public Map<String, Boolean> resetPw(
    @Valid @RequestBody EditMyPasswordForm editMyPasswordForm,
    @RequestHeader String Authorization
  ) {
    return userBusinessService.executeEditPassword(
      Authorization,
      editMyPasswordForm.prePassword(),
      editMyPasswordForm.newPassword()
    );
  }

  @Monitoring(option = USER)
  @PostMapping("login")
  @ResponseStatus(OK)
  public Map<String, String> mobileLogin(
    @Valid @RequestBody LoginForm loginForm) {
    return userBusinessService.executeLogin(
      loginForm.loginId(),
      loginForm.password()
    );
  }

  @Monitoring(option = USER)
  @PostMapping("client-login")
  @ResponseStatus(OK)
  public Map<String, String> clientLogin(
    @Valid @RequestBody LoginForm loginForm,
    HttpServletResponse response
  ) {
    Map<String, String> tokenPair = userBusinessService.executeLogin(
      loginForm.loginId(),
      loginForm.password()
    );

    Cookie refreshCookie = new Cookie("refreshToken", tokenPair.get("RefreshToken"));
    refreshCookie.setMaxAge(270 * 24 * 60 * 60);
    refreshCookie.setSecure(true);
    refreshCookie.setHttpOnly(true);
    response.addCookie(refreshCookie);

    return new HashMap<>() {{
      put("AccessToken", tokenPair.get("AccessToken"));
    }};
  }

  @Monitoring(option = USER)
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

  @Monitoring(option = USER)
  @GetMapping("/my-page")
  @ResponseStatus(OK)
  public UserInformationResponseForm myPage(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeLoadMyPage(Authorization);
  }

  @Monitoring(option = USER)
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

  @Monitoring(option = USER)
  @PostMapping("/refresh")
  @ResponseStatus(OK)
  public Map<String, String> tokenRefresh(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeJWTRefreshForMobileClient(Authorization);
  }

  // 회원 탈퇴
  @Monitoring(option = USER)
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

  @Monitoring(option = USER)
  @PostMapping("/favorite-major")
  @ResponseStatus(OK)
  public String saveFavoriteMajor(
    @RequestHeader String Authorization,
    @RequestBody FavoriteSaveDto favoriteSaveDto
  ) {
    userBusinessService.executeFavoriteMajorSave(Authorization, favoriteSaveDto);
    return "success";
  }

  @Monitoring(option = USER)
  @DeleteMapping("/favorite-major")
  @ResponseStatus(OK)
  public String deleteFavoriteMajor(@RequestHeader String Authorization, @RequestParam String majorType) {
    userBusinessService.executeFavoriteMajorDelete(Authorization, majorType);
    return "success";
  }

  @Monitoring(option = USER)
  @GetMapping("/favorite-major")
  @ResponseStatus(OK)
  public ResponseForm loadFavoriteMajor(@RequestHeader String Authorization) {
    return userBusinessService.executeFavoriteMajorLoad(Authorization);
  }

  @Monitoring(option = USER)
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

  @Monitoring(option = USER)
  @GetMapping("/restricted-reason")
  @ResponseStatus(OK)
  public List<LoadMyRestrictedReasonResponseForm> loadRestrictedReason(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeLoadRestrictedReason(Authorization);
  }

  @Monitoring(option = USER)
  @GetMapping("/blacklist-reason")
  @ResponseStatus(OK)
  public List<LoadMyBlackListReasonResponseForm> loadBlacklistReason(@Valid @RequestHeader String Authorization) {
    return userBusinessService.executeLoadBlackListReason(Authorization);
  }
}
