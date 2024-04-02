package usw.suwiki.domain.lecture.schedule.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USWTermResolver {

  private static final List<String> KEYWORDS_TO_REMOVE = List.of(
    "(재수강)", "재수강-", "재수강_", "재수강",
    "(비대면수업)", "비대면수업-", "비대면수업_", "비대면수업",
    "(대면수업)", "대면수업-", "대면수업_", "대면수업",
    "(혼합수업)", "혼합수업-", "혼합수업_", "혼합수업"
  );

  /**
   * 변환에 필요한 최소한의 스트링 형식입니다.
   * 해당 형식에 맞지 않는다면 수원대측에서 형식을 바꾼 것이므로 더 이상 호환되지 않기 때문에 예외를 발생시키고 적재를 중단해야 합니다.
   * https://regexr.com/7rq1g     "^([\\s가-힣A-Za-z\\d-]+\\([월화수목금토일]\\d+(?:,\\d+)*.*?\\))+$"
   * pass : "강의실107-1(수6,7,8)" "강의실 B215(화5,6,7 수5,6,7)"
   * pass : "(월1,2)" -> "미정(월1,2)"
   * fail : "강의실(1,2)" "강의실 월1,2" "강의실107(요일아님6,7,8)" "요일없음(1,2)" "강의실103(화5,6),강의실103"
   */
  private static final String ESTABLISHED_YEAR = "subjtEstbYear";
  private static final String ESTABLISHED_SEMESTER = "subjtEstbSmrCd";
  private static final String PLACE_SCHEDULE = "timtSmryCn";
  private static final String REPRESENT_PROFESSOR_NAME = "reprPrfsEnoNm";
  private static final String FACULTY_DIVISION = "facDvnm";
  private static final String SUBJECT_CODE = "subjtCd";
  private static final String SUBJECT_NAME = "subjtNm";
  private static final String EVALUATION_TYPE = "cretEvalNm";
  private static final String DIVIDE_CLASS_NUMBER = "diclNo";
  private static final String DEPARTMENT = "estbDpmjNm";
  private static final String POINT = "point";
  private static final String CAPACITY_TYPE = "capprTypeNm";
  private static final String TARGET_GRADE = "trgtGrdeCd";

  public static JsonLecture resolve(JSONObject json) {
    return JsonLecture.builder()
      .selectedSemester(parseSemester(json))
      .placeSchedule(String.valueOf(json.get(PLACE_SCHEDULE)))
      .professor(parseProfessorName(json))
      .lectureType(String.valueOf(json.get(FACULTY_DIVISION)))
      .lectureCode(String.valueOf(json.get(SUBJECT_CODE)))
      .lectureName(parseLectureName(json))
      .evaluateType(String.valueOf(json.get(EVALUATION_TYPE)))
      .dividedClassNumber(String.valueOf(json.get(DIVIDE_CLASS_NUMBER)))
      .majorType(parseMajorType(json))
      .point(Double.parseDouble(String.valueOf(json.get(POINT))))
      .capacityPresentationType(String.valueOf(json.get(CAPACITY_TYPE)))
      .grade(Integer.parseInt(String.valueOf(json.get(TARGET_GRADE))))
      .build();
  }

  private static String parseSemester(JSONObject json) {
    String year = String.valueOf(json.get(ESTABLISHED_YEAR));
    String semester = String.valueOf(json.get(ESTABLISHED_SEMESTER).toString().charAt(0));
    return year + "-" + semester;
  }

  private static String parseProfessorName(JSONObject json) {
    String professorName = String.valueOf(json.get(REPRESENT_PROFESSOR_NAME));
    return StringUtils.isEmpty(professorName) ? "-" : professorName;
  }

  private static String parseLectureName(JSONObject json) {
    var rawSubject = String.valueOf(json.get(SUBJECT_NAME));
    return removeUnnecessaryPatterns(rawSubject);
  }

  private static String removeUnnecessaryPatterns(String name) {
    if (name.contains("재수강-")) {
      int index = name.indexOf("(");
      name = name.substring(0, index);
    }

    for (String keyword : KEYWORDS_TO_REMOVE) {
      if (name.contains(keyword)) {
        name = name.replace(keyword, "");
      }
    }

    return name;
  }

  private static String parseMajorType(JSONObject json) {
    String majorType = String.valueOf(json.get(DEPARTMENT));
    return majorType.contains("·") ? majorType.replace("·", "-") : majorType;
  }
}
