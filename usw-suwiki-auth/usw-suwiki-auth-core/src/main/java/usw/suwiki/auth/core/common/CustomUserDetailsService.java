package usw.suwiki.auth.core.common;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import usw.suwiki.domain.user.model.UserAdapter;
import usw.suwiki.domain.user.service.UserAdapterService;

import java.util.List;

@Component
@RequiredArgsConstructor
class CustomUserDetailsService implements UserDetailsService {
  private final UserAdapterService userAdapterService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var userAdapter = userAdapterService.findByUsername(username);
    return toDetails(userAdapter);
  }

  private UserDetails toDetails(UserAdapter userAdapter) {
    return new User(
      String.valueOf(userAdapter.id()),
      null,
      List.of(new SimpleGrantedAuthority(userAdapter.role().name()))
    );
  }
}
