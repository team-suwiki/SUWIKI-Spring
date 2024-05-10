package usw.suwiki.domain.user.service;

import usw.suwiki.domain.user.model.UserAdapter;

public interface UserAdapterService {
  UserAdapter findByUsername(String username);
}
