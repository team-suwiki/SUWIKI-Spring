package usw.suwiki.domain.lecture.major;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMajorRepositoryV2 extends JpaRepository<FavoriteMajor, Long> {
  List<FavoriteMajor> findAllByUserId(Long userId);

  boolean existsByUserIdAndMajorType(Long userId, String majorType);

  Optional<FavoriteMajor> findByUserIdAndMajorType(Long userId, String majorType);
}
