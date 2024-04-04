package usw.suwiki.domain.user.isolated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserIsolationRepository extends JpaRepository<UserIsolation, Long> {

    Optional<UserIsolation> findByUserIdx(Long userIdx);

    Optional<UserIsolation> findByLoginId(String loginId);

    Optional<UserIsolation> findByEmail(String email);

    void deleteByLoginId(String loginId);

    void deleteByUserIdx(Long userIdx);

    List<UserIsolation> findByRequestedQuitDateBefore(LocalDateTime localDateTime);
}
