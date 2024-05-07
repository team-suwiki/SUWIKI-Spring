package usw.suwiki.api.lecture;

import usw.suwiki.domain.lecture.schedule.LectureSchedule;

public final class LectureScheduleFixture {

  public static LectureSchedule one(Long lectureId) {
    return new LectureSchedule(lectureId, "미래103(월7,8),미래B102(월5,6)", "2024-1");
  }
}
