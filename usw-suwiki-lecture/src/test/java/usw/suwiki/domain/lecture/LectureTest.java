package usw.suwiki.domain.lecture;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LectureTest {

  @Test
  void 강의_생성_성공() {
    // given

    // when
    var lecture = Lecture.builder()
      .semester("2021-2, 2022-1, 2022-2, 2024-1")
      .professor("교수님")
      .name("강의명")
      .majorType("교양")
      .type(Lecture.Type.선교)
      .lectureDetail(LectureDetail.builder()
        .code("9999")
        .point(3.0)
        .capprType("A형(강의식 수업)")
        .diclNo("001")
        .grade(1)
        .evaluateType(LectureDetail.Evaluation.상대평가)
        .build())
      .build();

    // then
    assertAll(
      () -> assertThat(lecture.getLectureEvaluationInfo()).isNotNull(),
      () -> assertThat(lecture.getPostsCount()).isZero()
    );
  }

}
