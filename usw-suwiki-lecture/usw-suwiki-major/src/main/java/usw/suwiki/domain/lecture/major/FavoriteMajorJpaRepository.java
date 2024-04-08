package usw.suwiki.domain.lecture.major;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FavoriteMajorJpaRepository implements FavoriteMajorRepository {
  private final EntityManager em;

  @Override
  public FavoriteMajor findById(Long id) {
    return em.find(FavoriteMajor.class, id);
  }

  @Override
  public List<FavoriteMajor> findAllByUser(Long userIdx) {
    return em.createQuery("SELECT f from FavoriteMajor f join f.user u WHERE u.id = :id")
      .setParameter("id", userIdx)
      .getResultList();
  }

  @Override
  public List<String> findOnlyMajorTypeByUser(Long userIdx) {
    return em.createQuery("SELECT f.majorType from FavoriteMajor f join f.user u WHERE u.id = :id")
      .setParameter("id", userIdx)
      .getResultList();
  }

  @Override
  public FavoriteMajor findByUserAndMajorType(Long userIdx, String majorType) {
    List resultList = em.createQuery(
        "SELECT f from FavoriteMajor f join f.user u WHERE u.id = :id AND f.majorType = :majorType")
      .setParameter("id", userIdx)
      .setParameter("majorType", majorType)
      .getResultList();

    return (FavoriteMajor) resultList.get(0);
  }

  @Override
  public void save(FavoriteMajor favoriteMajor) {
    em.persist(favoriteMajor);
  }

  @Override
  public void delete(FavoriteMajor favoriteMajor) {
    em.remove(favoriteMajor);
  }
}
