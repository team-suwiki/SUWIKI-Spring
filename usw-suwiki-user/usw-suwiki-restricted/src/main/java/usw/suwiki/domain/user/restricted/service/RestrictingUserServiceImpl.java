package usw.suwiki.domain.user.restricted.service;

import static usw.suwiki.domain.user.dto.UserAdminRequestDto.EvaluatePostRestrictForm;
import static usw.suwiki.domain.user.dto.UserAdminRequestDto.ExamPostRestrictForm;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.restricted.RestrictingUser;
import usw.suwiki.domain.user.restricted.RestrictingUserRepository;
import usw.suwiki.domain.user.service.BlacklistDomainCRUDService;
import usw.suwiki.domain.user.service.RestrictingUserService;
import usw.suwiki.domain.user.service.UserCRUDService;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class RestrictingUserServiceImpl implements RestrictingUserService {
  private static final String BANNED_REASON = "신고 누적으로 인한 블랙리스트";
  private static final String JUDGEMENT = "신고누적 블랙리스트 1년";
  private static final Long BANNED_PERIOD = 90L;

  private final UserCRUDService userCRUDService;
  private final BlacklistDomainCRUDService blacklistDomainCRUDService;
  private final RestrictingUserRepository restrictingUserRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Long> loadAllRestrictedUntilNow() {
    return restrictingUserRepository.findByRestrictingDateBefore(LocalDateTime.now()).stream()
      .map(RestrictingUser::getUserIdx)
      .toList();
  }

  @Override
  public void releaseByUserId(Long userId) {
    restrictingUserRepository.deleteByUserIdx(userId);
  }

  @Override
  public void executeRestrictUserFromEvaluatePost(EvaluatePostRestrictForm evaluatePostRestrictForm, Long reportedUserId) {
    User user = userCRUDService.loadUserFromUserIdx(reportedUserId);

    if (user.isCloseToArrest()) {
      blacklistDomainCRUDService.saveBlackListDomain(
        user.getId(),
        BANNED_PERIOD,
        BANNED_REASON,
        JUDGEMENT
      );
      return;
    }
    user.restrict();
    restrictingUserRepository.save(
        RestrictingUser.builder()
            .userIdx(user.getId())
            .restrictingDate(LocalDateTime.now().plusDays(evaluatePostRestrictForm.restrictingDate()))
            .restrictingReason(evaluatePostRestrictForm.restrictingReason())
            .judgement(evaluatePostRestrictForm.judgement())
            .build()
    );
  }

  @Override
  public void executeRestrictUserFromExamPost(ExamPostRestrictForm examPostRestrictForm, Long reportedUserId) {
    User user = userCRUDService.loadUserFromUserIdx(reportedUserId);
    if (user.isCloseToArrest()) {
      blacklistDomainCRUDService.saveBlackListDomain(
        user.getId(),
        BANNED_PERIOD,
        BANNED_REASON,
        JUDGEMENT
      );
      return;
    }
    user.restrict();
    restrictingUserRepository.save(
        RestrictingUser.builder()
            .userIdx(user.getId())
            .restrictingDate(LocalDateTime.now().plusDays(examPostRestrictForm.restrictingDate()))
            .restrictingReason(examPostRestrictForm.restrictingReason())
            .judgement(examPostRestrictForm.judgement())
            .build()
    );
  }
}
