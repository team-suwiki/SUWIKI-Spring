package usw.suwiki.domain.lecture.schedule.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureDetail;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.schedule.data.JsonLecture;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class LectureScheduleMapper {

  public static Lecture toLecture(JsonLecture jsonLecture) {
    return Lecture.builder()
      .name(jsonLecture.getLectureName())
      .type(jsonLecture.getLectureType())
      .professor(jsonLecture.getProfessor())
      .semester(jsonLecture.getSelectedSemester())
      .majorType(jsonLecture.getMajorType())
      .lectureDetail(
        LectureDetail.builder()
          .code(jsonLecture.getLectureCode())
          .grade(jsonLecture.getGrade())
          .point(jsonLecture.getPoint())
          .diclNo(jsonLecture.getDividedClassNumber())
          .evaluateType(jsonLecture.getEvaluateType())
          .capprType(jsonLecture.getCapacityPresentationType())
          .build())
      .build();
  }

  public static LectureResponse.Lecture toEmptyCellResponse(Lecture lecture) {
    return map(lecture, Collections.emptyList());
  }

  public static LectureResponse.Lecture toResponse(Lecture lecture, List<LectureResponse.LectureCell> cells) {
    return map(lecture, cells);
  }

  private static LectureResponse.Lecture map(Lecture lecture, List<LectureResponse.LectureCell> lectureCells) {
    return new LectureResponse.Lecture(
      lecture.getId(),
      lecture.getName(),
      lecture.getType(),
      lecture.getMajorType(),
      lecture.getGrade(),
      lecture.getProfessor(),
      lectureCells
    );
  }
}
