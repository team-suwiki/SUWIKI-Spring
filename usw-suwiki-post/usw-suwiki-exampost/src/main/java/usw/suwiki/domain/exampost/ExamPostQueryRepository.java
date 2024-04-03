package usw.suwiki.domain.exampost;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;
import usw.suwiki.domain.exampost.dto.QExamPostResponse_MyPost;

import java.util.List;

import static usw.suwiki.domain.exampost.QExamPost.examPost;
import static usw.suwiki.domain.lecture.QLecture.lecture;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamPostQueryRepository {
  private static final int LIMIT_PAGE_SIZE = 10;

  private final JPAQueryFactory queryFactory;

  public List<ExamPostResponse.MyPost> findAllByUserIdAndPageOption(Long userId, int page) {
    return queryFactory.select(new QExamPostResponse_MyPost(
        examPost.id,
        examPost.content,
        examPost.lectureInfo.lectureName,
        examPost.lectureInfo.selectedSemester,
        lecture.professor,
        lecture.majorType,
        lecture.semester,
        examPost.examDetail.examType,
        examPost.examDetail.examInfo,
        examPost.examDetail.examDifficulty))
      .from(examPost)
      .where(examPost.userIdx.eq(userId))
      .join(lecture).on(examPost.lectureInfo.lectureId.eq(lecture.id))
      .limit(LIMIT_PAGE_SIZE)
      .offset(page)
      .fetch();
  }
}
