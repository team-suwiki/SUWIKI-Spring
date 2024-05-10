package usw.suwiki.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.model.UserAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCRUDService implements UserAdapterService {
  private final UserRepository userRepository;

  public void saveUser(User user) {
    userRepository.save(user);
  }

  public List<User> loadUsersLastLoginBetweenStartEnd(LocalDateTime startTime, LocalDateTime endTime) {
    return userRepository.findByLastLoginBetween(startTime, endTime);
  }

  public User loadUserById(Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public Optional<User> loadWrappedUserFromUserIdx(Long userIdx) {
    return userRepository.findById(userIdx);
  }

  @Transactional(readOnly = true)
  public Optional<User> loadWrappedUserFromLoginId(String loginId) {
    return userRepository.findByLoginId(loginId);
  }

  @Transactional(readOnly = true)
  public Optional<User> loadWrappedUserFromEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional(readOnly = true)
  public User loadUserFromUserIdx(Long userIdx) {
    return convertOptionalUserToDomainUser(userRepository.findById(userIdx));
  }

  @Transactional(readOnly = true)
  public User loadUserFromLoginId(String loginId) {
    return convertOptionalUserToDomainUser(userRepository.findByLoginId(loginId));
  }

  @Transactional(readOnly = true)
  public User loadUserFromEmail(String email) {
    return convertOptionalUserToDomainUser(userRepository.findByEmail(email));
  }

  private User convertOptionalUserToDomainUser(Optional<User> optionalUser) {
    return optionalUser.orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_EXISTS));
  }

  @Transactional(readOnly = true)
  public long countAllUsers() {
    return userRepository.count();
  }

  public void deleteFromUserIdx(Long userIdx) {
    userRepository.deleteById(userIdx);
  }

  public void softDeleteForIsolation(Long userIdx) {
    User user = loadUserById(userIdx);
    user.sleep();
  }

  @Override
  public UserAdapter findByUsername(String username) {
    return userRepository.findByLoginId(username)
      .map(UserAdapter::from)
      .orElseThrow(() -> new AccountException(ExceptionType.USER_NOT_FOUND));
  }
}
