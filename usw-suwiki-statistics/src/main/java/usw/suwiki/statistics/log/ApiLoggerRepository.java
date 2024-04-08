package usw.suwiki.statistics.log;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

interface ApiLoggerRepository extends CrudRepository<ApiLogger, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<ApiLogger> findByCallDate(LocalDate callDate);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  ApiLogger save(ApiLogger apiLogger);
}
