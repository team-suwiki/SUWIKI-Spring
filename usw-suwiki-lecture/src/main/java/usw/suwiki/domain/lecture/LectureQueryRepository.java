package usw.suwiki.domain.lecture;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.model.Lectures;

import java.util.List;
import java.util.Optional;

import static usw.suwiki.domain.lecture.QLecture.lecture;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureQueryRepository {
  private static final int SLICE_LIMIT_PLUS_AMOUNT = 1;
  private static final String DEFAULT_ORDER = "modifiedDate";
  private static final Integer DEFAULT_LIMIT = 10;
  private static final Integer DEFAULT_PAGE = 1;

  private final JPAQueryFactory queryFactory;
  private final SemesterProvider semesterProvider;

  public Slice<Lecture> findCurrentSemesterLectures(
    final Long cursorId,
    final int limit,
    final String keyword,
    final String majorType,
    final Integer grade
  ) {
    var result = queryFactory.selectFrom(lecture)
      .where(
        isCursorGt(cursorId),
        containsKeywordInNameOrProfessor(keyword),
        isMajorTypeEq(majorType),
        isGradeEq(grade),
        lecture.semester.endsWith(semesterProvider.current())
      )
      .orderBy(lecture.id.asc())
      .limit(limit + SLICE_LIMIT_PLUS_AMOUNT)
      .fetch();

    if (result.size() > limit) {
      result.remove(limit);
    }

    return new SliceImpl<>(result, Pageable.ofSize(limit), result.size() > limit);
  }

  public Optional<Lecture> findByExtraUniqueKey(
    String lectureName,
    String professor,
    String majorType,
    String dividedClassNumber
  ) {
    var result = queryFactory.selectFrom(lecture)
      .where(lecture.name.eq(lectureName),
        lecture.professor.eq(professor),
        lecture.majorType.eq(majorType),
        lecture.lectureDetail.diclNo.eq(dividedClassNumber))
      .fetchOne();

    return Optional.ofNullable(result);
  }


  public Lectures findAllLecturesByOption(String searchValue, LectureSearchOption option) {
    Pageable pageable = PageRequest.of(page(option.getPageNumber()) - 1, DEFAULT_LIMIT);
    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(lecture.name.likeIgnoreCase("%" + searchValue + "%")
        .or(lecture.professor.likeIgnoreCase("%" + searchValue + "%")))
      .orderBy(
        createPostCountOption(),
        orderSpecifier(option.getOrderOption())
      )
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetchResults();

    return new Lectures(queryResults.getResults(), queryResults.getTotal());
  }

  public Lectures findAllLecturesByMajorType(String searchValue, LectureSearchOption option) {
    BooleanExpression searchCondition = lecture.majorType.eq(option.getMajorType())
      .and(lecture.name.likeIgnoreCase("%" + searchValue + "%")
        .or(lecture.professor.likeIgnoreCase("%" + searchValue + "%")));

    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .orderBy(
        createPostCountOption(),
        orderSpecifier(option.getOrderOption())
      )
      .offset((long) (page(option.getPageNumber()) - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public Lectures findAllLecturesByOption(LectureSearchOption option) {
    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .orderBy(
        createPostCountOption(),
        orderSpecifier(option.getOrderOption())
      )
      .offset((long) (page(option.getPageNumber()) - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public Lectures findAllLecturesByMajorType(LectureSearchOption option) {
    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(lecture.majorType.eq(option.getMajorType()))
      .orderBy(
        createPostCountOption(),
        orderSpecifier(option.getOrderOption())
      )
      .offset((long) (page(option.getPageNumber()) - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .where(lecture.majorType.eq(option.getMajorType()))
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public List<String> findAllMajorTypes() {
    return queryFactory.selectDistinct(lecture.majorType)
      .from(lecture)
      .fetch();
  }

  private BooleanExpression isCursorGt(Long cursorId) {
    return cursorId == null ? null : lecture.id.gt(cursorId);
  }

  private BooleanExpression containsKeywordInNameOrProfessor(String keyword) {
    return keyword == null ? null : lecture.name.contains(keyword).or(lecture.professor.contains(keyword));
  }

  private BooleanExpression isMajorTypeEq(String majorType) {
    return majorType == null ? null : lecture.majorType.eq(majorType);
  }

  private BooleanExpression isGradeEq(Integer grade) {
    return grade == null ? null : lecture.lectureDetail.grade.eq(grade);
  }

  private OrderSpecifier<?> orderSpecifier(String option) {
    return switch (orderOption(option)) {
      case "lectureEvaluationInfo.lectureSatisfactionAvg" ->
        lecture.lectureEvaluationInfo.lectureSatisfactionAvg.desc();
      case "lectureEvaluationInfo.lectureHoneyAvg" -> lecture.lectureEvaluationInfo.lectureHoneyAvg.desc();
      case "lectureEvaluationInfo.lectureLearningAvg" -> lecture.lectureEvaluationInfo.lectureLearningAvg.desc();
      case "lectureEvaluationInfo.lectureTotalAvg" -> lecture.lectureEvaluationInfo.lectureTotalAvg.desc();
      default -> lecture.modifiedDate.desc(); // Default order
    };
  }

  private String orderOption(String option) {
    if (option == null) {
      return DEFAULT_ORDER;
    }

    return option.equals(DEFAULT_ORDER) ? option : "lectureEvaluationInfo." + option;
  }

  private OrderSpecifier<Integer> createPostCountOption() {
    return new CaseBuilder()
      .when(lecture.postsCount.gt(0)).then(1)
      .otherwise(2)
      .asc();
  }

  private Integer page(Integer page) {
    return page == null ? DEFAULT_PAGE : page;
  }
}
