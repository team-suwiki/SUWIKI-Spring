package usw.suwiki.api.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.Lecture.Type;
import usw.suwiki.domain.lecture.LectureDetail;
import usw.suwiki.domain.lecture.LectureRepository;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class LecturePersister {
  private static final Random RANDOM = new Random();

  private final LectureRepository lectureRepository;

  public LectureBuilder builder() {
    return new LectureBuilder();
  }

  public final class LectureBuilder {

    private String semester;
    private String professor;
    private String name;
    private String majorType;
    private Type type;
    private LectureDetail lectureDetail;

    public LectureBuilder setSemester(String semester) {
      this.semester = semester;
      return this;
    }

    public LectureBuilder setProfessor(String professor) {
      this.professor = professor;
      return this;
    }

    public LectureBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public LectureBuilder setMajorType(String majorType) {
      this.majorType = majorType;
      return this;
    }

    public LectureBuilder setType(Type type) {
      this.type = type;
      return this;
    }

    public LectureBuilder setLectureDetail(LectureDetail lectureDetail) {
      this.lectureDetail = lectureDetail;
      return this;
    }

    public Lecture save() {
      Lecture lecture = Lecture.builder()
        .semester((semester == null ? "2021-1" : semester))
        .professor(professor == null ? "교수님" : professor)
        .name(name == null ? "강의명" : name)
        .majorType(majorType == null ? "교양" : majorType)
        .type(type == null ? Type.values()[RANDOM.nextInt(Type.values().length)] : type)
        .lectureDetail(lectureDetail == null ? LectureDetail.builder()
          .code(String.valueOf(RANDOM.nextInt(1000)))
          .point(new Double[]{2.0, 3.0, 1.0}[RANDOM.nextInt(3)])
          .capprType("A형(강의식 수업)")
          .diclNo("001")
          .grade(1)
          .evaluateType(LectureDetail.Evaluation.values()[RANDOM.nextInt(LectureDetail.Evaluation.values().length)])
          .build() : lectureDetail)
        .build();
      return lectureRepository.save(lecture);
    }

  }
}
