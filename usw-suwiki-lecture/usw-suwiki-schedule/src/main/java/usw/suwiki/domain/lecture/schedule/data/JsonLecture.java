package usw.suwiki.domain.lecture.schedule.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import usw.suwiki.domain.lecture.schedule.model.LectureInfo;

import java.util.Objects;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonLecture {
  private final String selectedSemester;
  private final String placeSchedule;
  private final String professor;
  private final int grade;
  private final String lectureType;
  private final String lectureCode;
  private final String lectureName;
  private final String evaluateType;
  private final String dividedClassNumber;
  private final String majorType;
  private final double point;
  private final String capacityPresentationType;

  public boolean isValidPlaceSchedule() {
    return !("null".equals(placeSchedule) || placeSchedule.isEmpty());
  }

  public boolean isInfoEquals(LectureInfo info) {
    return Objects.equals(info.name(), lectureName) &&
           Objects.equals(info.professor(), professor) &&
           Objects.equals(info.majorType(), majorType) &&
           Objects.equals(info.diclNo(), dividedClassNumber) &&
           info.placeSchedule().contains(placeSchedule);
  }
}
