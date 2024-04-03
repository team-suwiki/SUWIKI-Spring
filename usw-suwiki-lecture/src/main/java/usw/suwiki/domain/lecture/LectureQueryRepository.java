package usw.suwiki.domain.lecture;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.SlicePaginationUtils;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.model.Lectures;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static usw.suwiki.domain.lecture.QLecture.lecture;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureQueryRepository {
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
    JPAQuery<Lecture> query = queryFactory.selectFrom(lecture)
      .where(gtLectureCursorId(cursorId))
      .where(containsKeywordInNameOrProfessor(keyword))
      .where(eqMajorType(majorType))
      .where(eqGrade(grade))
      .where(lecture.semester.endsWith(semesterProvider.current()))
      .orderBy(lecture.id.asc())
      .limit(SlicePaginationUtils.increaseSliceLimit(limit));

    return SlicePaginationUtils.buildSlice(query.fetch(), limit);
  }

  public Optional<Lecture> findByExtraUniqueKey(
    String lectureName,
    String professor,
    String majorType,
    String dividedClassNumber
  ) {
    return Optional.ofNullable(
      queryFactory.selectFrom(lecture)
        .where(
          lecture.name.eq(lectureName),
          lecture.professor.eq(professor),
          lecture.majorType.eq(majorType),
          lecture.lectureDetail.diclNo.eq(dividedClassNumber))
        .fetchOne());
  }


  public Lectures findAllLecturesByOption(String searchValue, LectureSearchOption option) {
    String orderOption = initializeOrderOption(option.getOrderOption());
    Integer page = initializePageNumber(option.getPageNumber());

    BooleanExpression searchCondition = lecture.name
      .likeIgnoreCase("%" + searchValue + "%")
      .or(lecture.professor.likeIgnoreCase("%" + searchValue + "%"));

    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderOption);

    Pageable pageable = PageRequest.of(page - 1, DEFAULT_LIMIT);
    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .orderBy(
        createPostCountOption(),
        orderSpecifier
      )
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetchResults();

    return new Lectures(queryResults.getResults(), queryResults.getTotal());
  }

  public Lectures findAllLecturesByMajorType(String searchValue, LectureSearchOption option) {
    String orderOption = initializeOrderOption(option.getOrderOption());
    Integer page = initializePageNumber(option.getPageNumber());
    String majorType = option.getMajorType();

    BooleanExpression searchCondition = lecture.majorType.eq(majorType)
      .and(lecture.name.likeIgnoreCase("%" + searchValue + "%")
        .or(lecture.professor.likeIgnoreCase("%" + searchValue + "%")));

    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderOption);

    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .orderBy(
        createPostCountOption(),
        orderSpecifier
      )
      .offset((long) (page - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public Lectures findAllLecturesByOption(LectureSearchOption option) {
    String orderOption = initializeOrderOption(option.getOrderOption());
    Integer page = initializePageNumber(option.getPageNumber());

    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderOption);

    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .orderBy(
        createPostCountOption(),
        orderSpecifier
      )
      .offset((long) (page - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public Lectures findAllLecturesByMajorType(LectureSearchOption option) {
    String orderOption = initializeOrderOption(option.getOrderOption());
    Integer page = initializePageNumber(option.getPageNumber());
    String majorType = option.getMajorType();

    BooleanExpression searchCondition = lecture.majorType.eq(majorType);
    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderOption);

    QueryResults<Lecture> queryResults = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .orderBy(
        createPostCountOption(),
        orderSpecifier
      )
      .offset((long) (page - 1) * DEFAULT_LIMIT)
      .limit(DEFAULT_LIMIT)
      .fetchResults();

    long count = queryFactory
      .selectFrom(lecture)
      .where(searchCondition)
      .fetchCount();

    return new Lectures(queryResults.getResults(), count);
  }

  public List<String> findAllMajorTypes() {
    return queryFactory.selectDistinct(lecture.majorType)
      .from(lecture)
      .fetch();
  }

  private BooleanExpression gtLectureCursorId(Long cursorId) {
    return Objects.isNull(cursorId) ? null : lecture.id.gt(cursorId);
  }

  private BooleanExpression containsKeywordInNameOrProfessor(String keyword) {
    return Objects.isNull(keyword) ? null : lecture.name.contains(keyword).or(lecture.professor.contains(keyword));
  }

  private BooleanExpression eqMajorType(String majorType) {
    return Objects.isNull(majorType) ? null : lecture.majorType.eq(majorType);
  }

  private BooleanExpression eqGrade(Integer grade) {
    return Objects.isNull(grade) ? null : lecture.lectureDetail.grade.eq(grade);
  }

  private OrderSpecifier<?> getOrderSpecifier(String orderOption) {
    return switch (orderOption) {
      case "lectureEvaluationInfo.lectureSatisfactionAvg" ->
        lecture.lectureEvaluationInfo.lectureSatisfactionAvg.desc();
      case "lectureEvaluationInfo.lectureHoneyAvg" -> lecture.lectureEvaluationInfo.lectureHoneyAvg.desc();
      case "lectureEvaluationInfo.lectureLearningAvg" -> lecture.lectureEvaluationInfo.lectureLearningAvg.desc();
      case "lectureEvaluationInfo.lectureTotalAvg" -> lecture.lectureEvaluationInfo.lectureTotalAvg.desc();
      default -> lecture.modifiedDate.desc(); // Default order
    };
  }

  private OrderSpecifier<Integer> createPostCountOption() {
    return new CaseBuilder()
      .when(lecture.postsCount.gt(0)).then(1)
      .otherwise(2)
      .asc();
  }

  private String initializeOrderOption(String option) {
    if (option == null) {
      return DEFAULT_ORDER;
    }

    return option.equals(DEFAULT_ORDER) ? option : "lectureEvaluationInfo." + option;
  }

  private Integer initializePageNumber(Integer page) {
    return page == null ? DEFAULT_PAGE : page;
  }
}
