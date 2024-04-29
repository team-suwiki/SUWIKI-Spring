package usw.suwiki.domain.lecture.schedule.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static usw.suwiki.domain.lecture.dto.LectureResponse.LectureCell;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureStringConverter {
  private static final String LOCATION_NOT_SETTLED = "미정";
  private static final int START_POINTER = -1;
  private static final int NEXT_TIME = 1;

  /**
   * @param placeSchedules 강의 장소 및 시간 원본 lecture_schedule.place_schedule
   * @implNote place_schedule을 DTO 리스트로 변환
   */
  public static List<LectureCell> chunkToLectureCells(String placeSchedules) {
    List<LectureCell> lectureCells = new ArrayList<>();

    // e.g. "IT103(..),IT505(..)" -> [ "IT103(..)", "IT505(..)" ]
    for (String placeSchedule : placeSchedules.split(",(?![^()]*\\))")) {
      String location = extractLocatuon(placeSchedule);
      String schedules = removeBracket(placeSchedule);

      // e.g. "월1,2, 화1,2" -> [ "월1,2", "화1,2" ]
      for (String schedule : schedules.split(" ")) {
        String day = schedule.substring(0, 1);
        String stringPeriods = schedule.substring(1);

        for (List<Integer> periods : splitPeriods(stringPeriods)) {
          lectureCells.add(new LectureCell(location, toEnglish(day), periods.get(0), periods.get(periods.size() - 1)));
        }
      }
    }

    return lectureCells;
  }

  private static List<List<Integer>> splitPeriods(String stringPeriods) {
    List<List<Integer>> periods = new ArrayList<>();
    int start = START_POINTER;
    int previeous = Integer.MIN_VALUE;

    var times = Arrays.stream(stringPeriods.split(","))
      .map(Integer::parseInt)
      .sorted()
      .toList();

    for (int time : times) {
      if (start == START_POINTER || time != previeous + NEXT_TIME) {
        if (start != START_POINTER) {
          periods.add(times.subList(start, times.indexOf(previeous) + 1));
        }
        start = times.indexOf(time);
      }
      previeous = time;
    }

    if (start != START_POINTER) {
      periods.add(times.subList(start, times.size()));
    }

    return periods;
  }

  // e.g. "IT103(월1,2, 화1,2)" -> "IT103"
  private static String extractLocatuon(String locationAndDays) {
    String location = locationAndDays.split("\\(")[0];
    return location.isBlank() ? LOCATION_NOT_SETTLED : location;
  }

  // e.g. IT103(월1,2, 화1,2) -> "월1,2, 화1,2"
  private static String removeBracket(String locationAndDays) {
    int start = locationAndDays.indexOf('(') + 1;
    int end = locationAndDays.lastIndexOf(')');
    return locationAndDays.substring(start, end);
  }

  private static String toEnglish(String korean) {
    return switch (korean) {
      case "월" -> "MON";
      case "화" -> "TUE";
      case "수" -> "WED";
      case "목" -> "THU";
      case "금" -> "FRI";
      case "토" -> "SAT";
      case "일" -> "SUN";
      default -> throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_DAY);
    };
  }
}
