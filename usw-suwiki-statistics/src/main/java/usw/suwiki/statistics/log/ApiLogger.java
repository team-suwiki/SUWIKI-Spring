package usw.suwiki.statistics.log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiLogger {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  @Column(name = "lecture_api_call_time")
  private Long lectureApiCall = 0L;

  @Column
  @Builder.Default
  private Long lectureApiProcessAvg = 0L;

  @Builder.Default
  @Column(name = "evaluate_posts_api_call_time")
  private Long evaluatePostsApiCall = 0L;

  @Column
  @Builder.Default
  private Long evaluatePostsApiProcessAvg = 0L;

  @Builder.Default
  @Column(name = "exam_posts_api_call_time")
  private Long examPostsApiCall = 0L;

  @Column
  @Builder.Default
  private Long examPostsApiProcessAvg = 0L;

  @Builder.Default
  @Column(name = "user_api_call_time")
  private Long userApiCall = 0L;

  @Column
  @Builder.Default
  private Long userApiProcessAvg = 0L;

  @Builder.Default
  @Column(name = "notice_api_call_time")
  private Long noticeApiCall = 0L;

  @Column
  @Builder.Default
  private Long noticeApiProcessAvg = 0L;

  @Column
  @Builder.Default
  private LocalDate callDate = LocalDate.now();

  public static ApiLogger lecture(Long processTime) {
    return ApiLogger.builder()
      .lectureApiCall(1L)
      .lectureApiProcessAvg(processTime)
      .build();
  }

  public static ApiLogger evaluate(Long processTime) {
    return ApiLogger.builder()
      .evaluatePostsApiCall(1L)
      .evaluatePostsApiProcessAvg(processTime)
      .build();
  }

  public static ApiLogger exam(Long processTime) {
    return ApiLogger.builder()
      .examPostsApiCall(1L)
      .examPostsApiProcessAvg(processTime)
      .build();
  }

  public static ApiLogger user(Long processTime) {
    return ApiLogger.builder()
      .userApiCall(1L)
      .userApiProcessAvg(processTime)
      .build();
  }

  public static ApiLogger notice(Long processTime) {
    return ApiLogger.builder()
      .noticeApiCall(1L)
      .noticeApiProcessAvg(processTime)
      .build();
  }

  public ApiLogger logLecture(Long processTime) {
    lectureApiProcessAvg = (processTime + (lectureApiProcessAvg * lectureApiCall)) / (lectureApiCall + 1);
    lectureApiCall += 1;
    return this;
  }

  public ApiLogger logEvaluatePosts(Long processTime) {
    evaluatePostsApiProcessAvg = (processTime + (evaluatePostsApiProcessAvg * evaluatePostsApiCall)) / (evaluatePostsApiCall + 1);
    evaluatePostsApiCall += 1;
    return this;
  }

  public ApiLogger logExamPosts(Long processTime) {
    examPostsApiProcessAvg = (processTime + (examPostsApiProcessAvg * examPostsApiCall)) / (examPostsApiCall + 1);
    examPostsApiCall += 1;
    return this;
  }

  public ApiLogger logUser(Long processTime) {
    userApiProcessAvg = (processTime + (userApiProcessAvg * userApiCall)) / (userApiCall + 1);
    userApiCall += 1;
    return this;
  }

  public ApiLogger logNotice(Long processTime) {
    noticeApiProcessAvg = (processTime + (noticeApiProcessAvg * noticeApiCall)) / (noticeApiCall + 1);
    noticeApiCall += 1;
    return this;
  }
}
