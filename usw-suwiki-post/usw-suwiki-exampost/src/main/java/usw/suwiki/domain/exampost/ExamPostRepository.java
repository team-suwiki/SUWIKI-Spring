package usw.suwiki.domain.exampost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamPostRepository extends JpaRepository<ExamPost, Long> {

  List<ExamPost> findAllByUserIdx(Long userId);

  @Query(nativeQuery = true, value =
    "SELECT * FROM exam_post WHERE lecture_id = :lectureId limit :defaultLimit offset :page"
  )
  List<ExamPost> findAllByLectureIdAndPageOption(
    @Param("lectureId") Long lectureId,
    @Param("page") int page,
    @Param("defaultLimit") int defaultLimit
  );

  boolean existsByUserIdxAndLectureInfo_LectureId(Long userId, Long lectureId);
}
