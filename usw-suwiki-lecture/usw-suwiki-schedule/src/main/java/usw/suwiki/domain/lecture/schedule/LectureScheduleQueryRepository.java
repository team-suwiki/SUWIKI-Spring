package usw.suwiki.domain.lecture.schedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.schedule.model.LectureInfo;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureScheduleQueryRepository {
  private final JPAQueryFactory queryFactory;

  public List<LectureInfo> findAllLectureInfosBySemester(String semester) {
    return queryFactory.select(new QLectureInfo(
        lecture.id,
        lectureSchedule.id,
        lecture.name,
        lecture.professor,
        lecture.majorType,
        lecture.lectureDetail.diclNo,
        lectureSchedule.placeSchedule
      ))
      .from(lectureSchedule)
      .leftJoin(lecture).on(lecture.id.eq(lectureSchedule.lectureId))
      .where(lecture.semester.contains(semester))
      .fetch();
  }

  public List<LectureInfo> findAllLectureInfosById(Long lectureId) {
    return queryFactory.select(new QLectureInfo(
        lecture.id,
        lectureSchedule.id,
        lecture.name,
        lecture.professor,
        lecture.majorType,
        lecture.lectureDetail.diclNo,
        lectureSchedule.placeSchedule
      ))
      .from(lectureSchedule)
      .where(lecture.id.eq(lectureId))
      .leftJoin(lecture).on(lecture.id.eq(lectureSchedule.lectureId))
      .fetch();
  }
  
//  검증 이후 삭제
//  public List<LectureSchedule> findAllSchedulesBySemesterContains(String semester) {
//    return queryFactory.selectFrom(lectureSchedule)
//      .join(lectureSchedule.lecture).fetchJoin()
//      .where(lectureSchedule.lecture.semester.contains(semester))
//      .fetch();
//  }

//  public List<LectureSchedule> findAllByLectureId(Long lectureId) {
//    return queryFactory.selectFrom(lectureSchedule)
//      .where(lectureSchedule.lectureId.eq(lectureId))
//      .fetch();
//  }

  public List<String> findAllPlaceSchedulesByLectureId(Long lectureId) {
    return queryFactory.select(lectureSchedule.placeSchedule)
      .from(lectureSchedule)
      .where(lectureSchedule.lectureId.eq(lectureId))
      .fetch();
  }
}
