package usw.suwiki.domain.lecture.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;
import static usw.suwiki.domain.lecture.service.LectureStringConverter.toLectureCells;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureResponse {

  @Data
  public static class Simples {
    private final long count; // total
    private final List<Simple> data;
  }

  @Data
  public static class Simple {
    private final Long id;
    private final String semesterList;
    private final String professor;
    private final String lectureType;
    private final String lectureName;
    private final String majorType;
    private final float lectureTotalAvg;
    private final float lectureSatisfactionAvg;
    private final float lectureHoneyAvg;
    private final float lectureLearningAvg;
  }

  @Data
  public static class Detail {
    private final Long id;
    private final String semesterList;
    private final String professor;
    private final String lectureType;
    private final String lectureName;
    private final String majorType;
    private final float lectureTotalAvg;
    private final float lectureSatisfactionAvg;
    private final float lectureHoneyAvg;
    private final float lectureLearningAvg;
    private final float lectureTeamAvg;
    private final float lectureDifficultyAvg;
    private final float lectureHomeworkAvg;
  }

  @Data
  public static class ScheduledLecture {
    private final Boolean isLast;
    private final List<Lecture> content;

    public static ScheduledLecture of(int size, List<Lecture> content) {
      return new ScheduledLecture(
        content.size() < size,
        content.size() > size ? content.subList(0, size) : content
      );
    }
  }

  @Data
  public static class Lecture {
    private final Long id;
    private final String name;
    private final String type;
    private final String major;
    private final int grade;
    private final String professorName;
    private final List<LectureCell> originalCellList; // todo: 프론트에 빌어서 이름 바꾸기

    @QueryProjection
    public Lecture(Long id, String name, String type, String major, int grade, String professorName, String placeSchedules) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.major = major;
      this.grade = grade;
      this.professorName = professorName;
      this.originalCellList = placeSchedules == null ? emptyList() : toLectureCells(placeSchedules);
    }
  }

  @Data
  public static class LectureCell {
    private final String location;
    private final String day;
    private final Integer startPeriod;
    private final Integer endPeriod;
  }
}
