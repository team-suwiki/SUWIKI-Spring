package usw.suwiki.domain.lecture.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.SemesterProvider;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.QLectureResponse_Lecture;
import usw.suwiki.domain.lecture.schedule.model.LectureInfo;
import usw.suwiki.domain.lecture.schedule.model.QLectureInfo;

import java.util.List;

import static usw.suwiki.domain.lecture.QLecture.lecture;
import static usw.suwiki.domain.lecture.schedule.QLectureSchedule.lectureSchedule;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureScheduleQueryRepository {
  private static final int CHECK_NEXT_VALUE = 1;

  private final JPAQueryFactory queryFactory;
  private final SemesterProvider semesterProvider;

  public LectureResponse.ScheduledLecture findCurrentSemesterLectures(Long cursorId, int size, String keyword, String major, Integer grade) {
    var result = queryFactory.select(new QLectureResponse_Lecture(
        lecture.id,
        lecture.name,
        lecture.type.stringValue(),
        lecture.majorType,
        lecture.lectureDetail.grade,
        lecture.professor,
        lectureSchedule.placeSchedule
      ))
      .from(lecture)
      .leftJoin(lectureSchedule).on(lectureSchedule.lectureId.eq(lecture.id))
      .where(
        isCursorGt(cursorId),
        containsNameOrProfessor(keyword),
        isMajorTypeEq(major),
        isGradeEq(grade),
        lecture.semester.endsWith(semesterProvider.current())
      )
      .limit(size + CHECK_NEXT_VALUE)
      .fetch();

    return LectureResponse.ScheduledLecture.of(size, result);
  }

  private BooleanExpression isCursorGt(Long cursorId) {
    return cursorId == null ? null : lecture.id.gt(cursorId);
  }

  private BooleanExpression containsNameOrProfessor(String keyword) {
    return keyword == null ? null : lecture.name.contains(keyword).or(lecture.professor.contains(keyword));
  }

  private BooleanExpression isMajorTypeEq(String majorType) {
    return majorType == null ? null : lecture.majorType.eq(majorType);
  }

  private BooleanExpression isGradeEq(Integer grade) {
    return grade == null ? null : lecture.lectureDetail.grade.eq(grade);
  }

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
}
