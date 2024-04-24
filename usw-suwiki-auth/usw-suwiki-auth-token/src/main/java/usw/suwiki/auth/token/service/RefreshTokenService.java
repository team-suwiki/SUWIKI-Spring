package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.RefreshToken;
import usw.suwiki.auth.token.RefreshTokenRepository;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public void save(Long userId, String token) {
    var refreshToken = new RefreshToken(userId, token);
    refreshTokenRepository.save(refreshToken);
  }

  @Transactional
  public void deleteByUserId(Long userId) {
    refreshTokenRepository.deleteByUserIdx(userId);
  }

  public Optional<RefreshToken> loadByUserId(Long userId) {
    return refreshTokenRepository.findByUserIdx(userId);
  }

  public RefreshToken loadByPayload(String payload) {
    return refreshTokenRepository.findByPayload(payload)
      .orElseThrow(() -> new AccountException(ExceptionType.INVALID_TOKEN));
  }
}
