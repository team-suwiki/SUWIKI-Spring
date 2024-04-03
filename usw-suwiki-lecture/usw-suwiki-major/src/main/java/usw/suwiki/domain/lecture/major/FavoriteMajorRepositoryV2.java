package usw.suwiki.domain.lecture.major;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMajorRepositoryV2 extends JpaRepository<FavoriteMajor, Long> {
  List<FavoriteMajor> findAllByUserIdx(Long userId);

  boolean existsByUserIdxAndMajorType(Long userId, String majorType);

  Optional<FavoriteMajor> findByUserIdxAndMajorType(Long userId, String majorType);
}
