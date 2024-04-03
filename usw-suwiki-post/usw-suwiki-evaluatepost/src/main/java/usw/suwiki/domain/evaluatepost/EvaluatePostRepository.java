package usw.suwiki.domain.evaluatepost;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluatePostRepository extends JpaRepository<EvaluatePost, Long> {

  List<EvaluatePost> findAllByUserIdx(Long userId);

  boolean existsByUserIdxAndLectureInfo_LectureId(Long userId, Long lectureId);
}
