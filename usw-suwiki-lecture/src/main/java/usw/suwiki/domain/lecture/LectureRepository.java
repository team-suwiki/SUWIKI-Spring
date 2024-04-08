package usw.suwiki.domain.lecture;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

  // TODO: 낙관적 락은 어떨지 고민해보기 (김영한님의 추천은 READ COMMITTED + 낙관적 락)
  // TODO: timeout 필요성 동시성 테스트로 확인하기
  //    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="10000")})  // timeout: 10s
  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  Optional<Lecture> findForUpdateById(Long lectureId);

  List<Lecture> findAllBySemesterContains(String semester);
}
