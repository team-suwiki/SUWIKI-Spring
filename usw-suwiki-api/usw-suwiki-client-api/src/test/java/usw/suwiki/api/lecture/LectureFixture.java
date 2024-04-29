package usw.suwiki.api.lecture;

import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureDetail;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public final class LectureFixture {
  private static final Random RANDOM = new Random();
  private static final List<String> semesters = List.of("2021-2", "2022-1", "2022-2", "2023-1", "2023-2", "2024-1");

  public static Lecture one() {
    return Lecture.builder()
      .semester(randomSemesters())
      .professor("교수님")
      .name("강의명")
      .majorType("교양")
      .type(Lecture.Type.values()[RANDOM.nextInt(Lecture.Type.values().length)])
      .lectureDetail(LectureDetail.builder()
        .code(String.valueOf(RANDOM.nextInt(1000)))
        .point(new Double[]{2.0, 3.0, 1.0}[RANDOM.nextInt(3)])
        .capprType("A형(강의식 수업)")
        .diclNo("001")
        .grade(1)
        .evaluateType(LectureDetail.Evaluation.values()[RANDOM.nextInt(LectureDetail.Evaluation.values().length)])
        .build())
      .build();
  }

  public static List<Lecture> list(int size) {
    return IntStream.range(0, size)
      .mapToObj(i -> one())
      .toList();
  }

  private static String randomSemesters() {
    return String.join(", ", semesters.subList(0, RANDOM.nextInt(semesters.size()) + 1));
  }
}
