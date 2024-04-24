package usw.suwiki.domain.user.service;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.auth.token.RefreshToken;
import usw.suwiki.auth.token.service.ConfirmationTokenCRUDService;
import usw.suwiki.auth.token.service.RefreshTokenService;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.mail.EmailSender;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.dto.FavoriteSaveDto;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static usw.suwiki.common.response.ApiResponseFactory.overlapFalseFlag;
import static usw.suwiki.common.response.ApiResponseFactory.overlapTrueFlag;
import static usw.suwiki.common.response.ApiResponseFactory.successFlag;
import static usw.suwiki.core.mail.MailType.EMAIL_AUTH;
import static usw.suwiki.core.mail.MailType.FIND_ID;
import static usw.suwiki.core.mail.MailType.FIND_PASSWORD;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyBlackListReasonResponseForm;
import static usw.suwiki.domain.user.dto.UserResponseDto.LoadMyRestrictedReasonResponseForm;
import static usw.suwiki.domain.user.dto.UserResponseDto.UserInformationResponseForm;

@Service
@Transactional
@RequiredArgsConstructor
public class UserBusinessService {
  private static final String MAIL_FORM = "@suwon.ac.kr";

  private final EmailSender emailSender;
  private final PasswordEncoder passwordEncoder;

  private final UserCRUDService userCRUDService;
  private final BlacklistDomainService blacklistDomainService;
  private final UserIsolationCRUDService userIsolationCRUDService;
  private final BlacklistDomainCRUDService blacklistDomainCRUDService;
  private final RestrictingUserCRUDService restrictingUserCRUDService;

  private final FavoriteMajorService favoriteMajorService;

  private final ClearReportService clearReportService;
  private final ClearViewExamService clearViewExamService;
  private final ClearExamPostsService clearExamPostsService;
  private final ClearEvaluatePostsService clearEvaluatePostsService;

  private final RefreshTokenService refreshTokenService;
  private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

  private final TokenAgent tokenAgent;

  @Transactional(readOnly = true)
  public Map<String, Boolean> executeCheckId(String loginId) {
    if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() ||
        userIsolationCRUDService.isIsolatedByLoginId(loginId)
    ) {
      return overlapTrueFlag();
    }
    return overlapFalseFlag();
  }

  @Transactional(readOnly = true)
  public Map<String, Boolean> executeCheckEmail(String email) {
    if (userCRUDService.loadWrappedUserFromEmail(email).isPresent() ||
        userIsolationCRUDService.isIsolatedByEmail(email)
    ) {
      return overlapTrueFlag();
    }
    return overlapFalseFlag();
  }

  public void wroteEvaluation(Long userId) {
    User user = userCRUDService.loadUserById(userId);
    user.writeEvaluatePost();
  }

  public void deleteEvaluation(Long userId) {
    User user = userCRUDService.loadUserById(userId);
    user.deleteEvaluatePost();
  }

  public Map<String, Boolean> executeJoin(String loginId, String password, String email) {
    blacklistDomainService.isUserInBlackListThatRequestJoin(email);

    if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent() ||
        userIsolationCRUDService.isIsolatedByLoginId(loginId) ||
        userCRUDService.loadWrappedUserFromEmail(email).isPresent() ||
        userIsolationCRUDService.isIsolatedByEmail(email)
    ) {
      throw new AccountException(ExceptionType.LOGIN_ID_OR_EMAIL_OVERLAP);
    }

    if (!email.contains(MAIL_FORM)) {
      throw new AccountException(ExceptionType.IS_NOT_EMAIL_FORM);
    }

    User user = User.init(loginId, passwordEncoder.encode(password), email);
    userCRUDService.saveUser(user);

    ConfirmationToken confirmationToken = new ConfirmationToken(user.getId());
    confirmationTokenCRUDService.saveConfirmationToken(confirmationToken);

    emailSender.send(email, EMAIL_AUTH, confirmationToken.getToken());
    return successFlag();
  }

  public Map<String, Boolean> executeFindId(String email) {
    Optional<User> requestUser = userCRUDService.loadWrappedUserFromEmail(email);
    Optional<String> isolatedLoginId = userIsolationCRUDService.getIsolatedLoginIdByEmail(email);

    if (requestUser.isPresent()) {
      emailSender.send(email, FIND_ID, requestUser.get().getLoginId());
      return successFlag();
    } else if (isolatedLoginId.isPresent()) {
      emailSender.send(email, FIND_ID, isolatedLoginId.get());
      return successFlag();
    }
    throw new AccountException(ExceptionType.USER_NOT_EXISTS);
  }

  // todo: isoloation user table부터 확인 후 user table 확인하도록 수정
  public Map<String, Boolean> executeFindPw(String loginId, String email) {
    Optional<User> userByLoginId = userCRUDService.loadWrappedUserFromLoginId(loginId);

    if (userByLoginId.isEmpty()) {
      throw new AccountException(ExceptionType.USER_NOT_FOUND_BY_LOGINID);
    }

    Optional<User> userByEmail = userCRUDService.loadWrappedUserFromEmail(email);
    if (userByEmail.isEmpty()) {
      throw new AccountException(ExceptionType.USER_NOT_FOUND_BY_EMAIL);
    }

    if (userByLoginId.equals(userByEmail)) {
      User user = userByLoginId.get();
      emailSender.send(email, FIND_PASSWORD, user.updateRandomPassword(passwordEncoder));
      return successFlag();
    } else if (userIsolationCRUDService.isRetrievedUserEquals(email, loginId)) {
      String newPassword = userIsolationCRUDService.updateIsolatedUserPassword(passwordEncoder, email);
      emailSender.send(email, FIND_PASSWORD, newPassword);
      return successFlag();
    }
    throw new AccountException(ExceptionType.USER_NOT_FOUND_BY_EMAIL);
  }

  public Map<String, String> executeLogin(String loginId, String inputPassword) {
    if (userCRUDService.loadWrappedUserFromLoginId(loginId).isPresent()) {
      User user = userCRUDService.loadUserFromLoginId(loginId);
      user.isUserEmailAuthed(confirmationTokenCRUDService.loadConfirmationTokenFromUserIdx(user.getId()));
      if (user.validatePassword(passwordEncoder, inputPassword)) {
        user.login();
        return generateJwt(user);
      }
    } else if (userIsolationCRUDService.isLoginableIsolatedUser(loginId, inputPassword, passwordEncoder)) {
      User user = userIsolationCRUDService.awakeIsolated(userCRUDService, loginId);
      return generateJwt(user);
    }
    throw new AccountException(ExceptionType.PASSWORD_ERROR);
  }


  public Map<String, Boolean> executeEditPassword(String Authorization, String prePassword, String newPassword) {
    User user = userCRUDService.loadUserFromUserIdx(tokenAgent.parseId(Authorization));

    if (!passwordEncoder.matches(prePassword, user.getPassword())) {
      throw new AccountException(ExceptionType.PASSWORD_ERROR);
    } else if (prePassword.equals(newPassword)) {
      throw new AccountException(ExceptionType.PASSWORD_NOT_CHANGED);
    }
    user.updatePassword(passwordEncoder, newPassword);
    return successFlag();
  }

  public UserInformationResponseForm executeLoadMyPage(String Authorization) {
    Long userIdx = tokenAgent.parseId(Authorization);
    User user = userCRUDService.loadUserFromUserIdx(userIdx);
    return UserInformationResponseForm.buildMyPageResponseForm(user);
  }

  public Map<String, String> executeJWTRefreshForWebClient(Cookie requestRefreshCookie) {
    String payload = requestRefreshCookie.getValue();
    RefreshToken refreshToken = refreshTokenService.loadByPayload(payload);
    User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
    return refreshJwt(user, payload);
  }

  public Map<String, String> executeJWTRefreshForMobileClient(String payload) {
    RefreshToken refreshToken = refreshTokenService.loadByPayload(payload);
    User user = userCRUDService.loadUserFromUserIdx(refreshToken.getUserIdx());
    return refreshJwt(user, refreshToken.getPayload());
  }

  public Map<String, Boolean> executeQuit(String authorization, String inputPassword) {
    User user = userCRUDService.loadUserFromUserIdx(tokenAgent.parseId(authorization));

    if (!user.validatePassword(passwordEncoder, inputPassword)) {
      throw new AccountException(ExceptionType.PASSWORD_ERROR);
    }

    favoriteMajorService.clear(user.getId());
    clearReportService.clear(user.getId());
    clearViewExamService.clear(user.getId());
    clearExamPostsService.clear(user.getId());
    clearEvaluatePostsService.clear(user.getId());

    user.waitQuit();
    return successFlag();
  }

  public List<LoadMyBlackListReasonResponseForm> executeLoadBlackListReason(String Authorization) {
    User requestUser = userCRUDService.loadUserFromUserIdx(tokenAgent.parseId(Authorization));
    return blacklistDomainCRUDService.loadAllBlacklistLog(requestUser.getId());
  }

  public List<LoadMyRestrictedReasonResponseForm> executeLoadRestrictedReason(String Authorization) {
    User requestUser = userCRUDService.loadUserFromUserIdx(tokenAgent.parseId(Authorization));
    return restrictingUserCRUDService.loadRestrictedLog(requestUser.getId());
  }

  public void executeFavoriteMajorSave(String authorization, FavoriteSaveDto favoriteSaveDto) {
    tokenAgent.validateRestrictedUser(authorization);
    Long userId = tokenAgent.parseId(authorization);

    favoriteMajorService.save(userId, favoriteSaveDto.getMajorType());
  }

  public void executeFavoriteMajorDelete(String authorization, String majorType) {
    tokenAgent.validateRestrictedUser(authorization);
    Long userIdx = tokenAgent.parseId(authorization);
    favoriteMajorService.delete(userIdx, majorType);
  }

  public ResponseForm executeFavoriteMajorLoad(String authorization) {
    tokenAgent.validateRestrictedUser(authorization);
    Long userIdx = tokenAgent.parseId(authorization);
    List<String> list = favoriteMajorService.findMajorTypeByUser(userIdx);
    return new ResponseForm(list);
  }

  private void rollBackUserFromSleeping(Long userIdx, String loginId, String password, String email) {
    User user = userCRUDService.loadUserFromUserIdx(userIdx);
    user.awake(loginId, password, email);
  }

  private Map<String, String> generateJwt(User user) {
    Claim userClaim = new UserClaim(user.getLoginId(), user.getRole().name(), user.getRestricted());

    return new HashMap<>() {{
      put("AccessToken", tokenAgent.createAccessToken(user.getId(), userClaim));
      put("RefreshToken", tokenAgent.login(user.getId()));
    }};
  }

  private Map<String, String> refreshJwt(User user, String refreshTokenPayload) {
    Claim userClaim = new UserClaim(user.getLoginId(), user.getRole().name(), user.getRestricted());

    return new HashMap<>() {{
      put("AccessToken", tokenAgent.createAccessToken(user.getId(), userClaim));
      put("RefreshToken", tokenAgent.reissue(refreshTokenPayload));
    }};
  }
}
