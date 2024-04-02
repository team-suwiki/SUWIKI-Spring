package usw.suwiki.domain.lecture.schedule.model;

import com.querydsl.core.annotations.QueryProjection;

public record LectureInfo(
  Long id,
  Long scheduleId,
  String name,
  String professor,
  String majorType,
  String diclNo,
  String placeSchedule
) {

  @QueryProjection
  public LectureInfo(Long id, Long scheduleId, String name, String professor, String majorType, String diclNo, String placeSchedule) {
    this.id = id;
    this.scheduleId = scheduleId;
    this.name = name;
    this.professor = professor;
    this.majorType = majorType;
    this.diclNo = diclNo;
    this.placeSchedule = placeSchedule;
  }
}
