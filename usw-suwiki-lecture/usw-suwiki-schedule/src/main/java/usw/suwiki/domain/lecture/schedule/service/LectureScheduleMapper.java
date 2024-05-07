package usw.suwiki.domain.lecture.schedule.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureDetail;
import usw.suwiki.domain.lecture.schedule.data.JsonLecture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class LectureScheduleMapper {

  public static Lecture toLecture(JsonLecture jsonLecture) {
    return Lecture.builder()
      .name(jsonLecture.getLectureName())
      .type(Lecture.Type.valueOf(jsonLecture.getLectureType()))
      .professor(jsonLecture.getProfessor())
      .semester(jsonLecture.getSelectedSemester())
      .majorType(jsonLecture.getMajorType())
      .lectureDetail(
        LectureDetail.builder()
          .code(jsonLecture.getLectureCode())
          .grade(jsonLecture.getGrade())
          .point(jsonLecture.getPoint())
          .diclNo(jsonLecture.getDividedClassNumber())
          .evaluateType(LectureDetail.Evaluation.valueOf(jsonLecture.getEvaluateType()))
          .capprType(jsonLecture.getCapacityPresentationType())
          .build())
      .build();
  }
}
