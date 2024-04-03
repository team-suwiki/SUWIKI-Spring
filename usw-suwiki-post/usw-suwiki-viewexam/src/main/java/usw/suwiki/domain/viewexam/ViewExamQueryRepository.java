package usw.suwiki.domain.viewexam;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import usw.suwiki.domain.viewexam.dto.QViewExamResponse_PurchaseHistory;
import usw.suwiki.domain.viewexam.dto.ViewExamResponse;

import java.util.List;

import static usw.suwiki.domain.lecture.QLecture.lecture;
import static usw.suwiki.domain.viewexam.QViewExam.viewExam;

@Repository
@RequiredArgsConstructor
public class ViewExamQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<ViewExamResponse.PurchaseHistory> loadPurchasedHistoriesByUserId(Long userId) {
    return queryFactory.select(new QViewExamResponse_PurchaseHistory(
        viewExam.id,
        lecture.professor,
        lecture.name,
        lecture.majorType,
        viewExam.createDate
      ))
      .from(viewExam)
      .where(viewExam.userIdx.eq(userId))
      .join(lecture).on(viewExam.lectureId.eq(lecture.id))
      .fetch();
  }
}
