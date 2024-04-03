package usw.suwiki.domain.evaluatepost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.dto.EvaluatePostResponse;
import usw.suwiki.domain.evaluatepost.dto.QEvaluatePostResponse_Detail;
import usw.suwiki.domain.evaluatepost.dto.QEvaluatePostResponse_MyPost;

import java.util.List;

import static usw.suwiki.domain.evaluatepost.QEvaluatePost.evaluatePost;
import static usw.suwiki.domain.lecture.QLecture.lecture;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluatePostQueryRepository {
  private static final int LIMIT_PAGE_SIZE = 10;

  private final JPAQueryFactory queryFactory;

  public List<EvaluatePostResponse.Detail> findAllByLectureIdAndPageOption(Long lectureId, int page) {
    return queryFactory.select(new QEvaluatePostResponse_Detail(
        evaluatePost.id,
        evaluatePost.content,
        evaluatePost.lectureInfo.selectedSemester,
        evaluatePost.lectureRating.totalAvg,
        evaluatePost.lectureRating.satisfaction,
        evaluatePost.lectureRating.learning,
        evaluatePost.lectureRating.honey,
        evaluatePost.lectureRating.team,
        evaluatePost.lectureRating.difficulty,
        evaluatePost.lectureRating.homework))
      .from(evaluatePost)
      .where(evaluatePost.lectureInfo.lectureId.eq(lectureId))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }

  public List<EvaluatePostResponse.MyPost> findAllByUserIdAndPageOption(Long userId, int page) {
    return queryFactory.select(new QEvaluatePostResponse_MyPost(
        evaluatePost.id,
        evaluatePost.content,
        evaluatePost.lectureInfo.lectureName,
        evaluatePost.lectureInfo.professor,
        lecture.majorType,
        evaluatePost.lectureInfo.selectedSemester,
        lecture.semester,
        evaluatePost.lectureRating.totalAvg,
        evaluatePost.lectureRating.satisfaction,
        evaluatePost.lectureRating.learning,
        evaluatePost.lectureRating.honey,
        evaluatePost.lectureRating.team,
        evaluatePost.lectureRating.difficulty,
        evaluatePost.lectureRating.homework))
      .from(evaluatePost)
      .join(lecture).on(evaluatePost.lectureInfo.lectureId.eq(lecture.id))
      .where(evaluatePost.userIdx.eq(userId))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }
}
