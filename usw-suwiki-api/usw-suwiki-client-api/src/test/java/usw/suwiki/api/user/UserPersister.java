package usw.suwiki.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;

@Component
@RequiredArgsConstructor
public class UserPersister {

  private final UserRepository userRepository;

  public UserBuilder builder() {
    return new UserBuilder();
  }

  public final class UserBuilder {

    private String loginId;
    private String password;
    private String email;

    public UserBuilder setLoginId(String loginId) {
      this.loginId = loginId;
      return this;
    }

    public UserBuilder setPassword(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder setEmail(String email) {
      this.email = email;
      return this;
    }

    public User save() {
      User user = initUser();
      user.activate();

      return userRepository.save(user);
    }

    public User saveRestrictUser() {
      User user = initUser();

      return userRepository.save(user);
    }

    private User initUser() {
      User user = User.init(
        (loginId == null ? "loginId" : loginId),
        (password == null ? "password" : password),
        (email == null ? "email" : email)
      );
      return user;
    }
  }
}
