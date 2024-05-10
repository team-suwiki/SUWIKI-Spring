package usw.suwiki.domain.lecture.major.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.FavoriteMajorException;
import usw.suwiki.domain.lecture.major.FavoriteMajor;
import usw.suwiki.domain.lecture.major.FavoriteMajorRepositoryV2;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteMajorServiceV2 {
  private final FavoriteMajorRepositoryV2 favoriteMajorRepositoryV2;

  public void save(Long userId, String majorType) {
    validateDuplicateFavoriteMajor(userId, majorType);
    favoriteMajorRepositoryV2.save(new FavoriteMajor(userId, majorType));
  }

  private void validateDuplicateFavoriteMajor(Long userId, String majorType) {
    if (favoriteMajorRepositoryV2.existsByUserIdxAndMajorType(userId, majorType)) {
      throw new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_DUPLICATE_REQUEST);
    }
  }

  public List<String> findAllMajorTypeByUser(Long userId) {
    List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserIdx(userId);
    return favoriteMajors.stream().map(FavoriteMajor::getMajorType).toList();
  }

  public void delete(Long userId, String majorType) {
    FavoriteMajor favoriteMajor = favoriteMajorRepositoryV2.findByUserIdxAndMajorType(userId, majorType)
      .orElseThrow(() -> new FavoriteMajorException(ExceptionType.FAVORITE_MAJOR_NOT_FOUND));

    favoriteMajorRepositoryV2.delete(favoriteMajor);
  }

  public void deleteAllFromUserId(Long userId) {
    List<FavoriteMajor> favoriteMajors = favoriteMajorRepositoryV2.findAllByUserIdx(userId);
    favoriteMajorRepositoryV2.deleteAll(favoriteMajors);
  }
}
