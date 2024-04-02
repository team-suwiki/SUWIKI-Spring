package usw.suwiki.auth.token;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static usw.suwiki.auth.token.QConfirmationToken.confirmationToken;

@Repository
@RequiredArgsConstructor
public class ConfirmationTokenQueryDslRepositoryImpl implements ConfirmationTokenQueryDslRepository {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<ConfirmationToken> loadNotConfirmedTokensByExpiresAtIsNull(LocalDateTime localDateTime) {
    return queryFactory
      .selectFrom(confirmationToken)
      .where(confirmationToken.expiresAt.before(localDateTime))
      .where(confirmationToken.confirmedAt.isNull())
      .fetch();
  }
}
