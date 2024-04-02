package usw.suwiki.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.auth.token.ConfirmationToken;

import static usw.suwiki.auth.token.response.ConfirmResponse.ERROR;
import static usw.suwiki.auth.token.response.ConfirmResponse.EXPIRED;
import static usw.suwiki.auth.token.response.ConfirmResponse.SUCCESS;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmationTokenBusinessService {
  private final ConfirmUserService confirmUserService;
  private final ConfirmationTokenCRUDService confirmationTokenCRUDService;

  public String confirmToken(String token) {
    return confirmationTokenCRUDService.loadConfirmationTokenFromPayload(token)
      .map(this::confirm)
      .orElseGet(ERROR::getContent);
  }

  private String confirm(ConfirmationToken token) {
    if (token.isTokenExpired()) {
      confirmationTokenCRUDService.deleteFromId(token.getId());
      confirmUserService.delete(token.getUserIdx());
      return EXPIRED.getContent();
    }

    token.confirmed();
    confirmUserService.activated(token.getUserIdx());
    return SUCCESS.getContent();
  }
}
