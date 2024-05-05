package usw.suwiki.api.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.api.lecture.LecturePersister;
import usw.suwiki.api.user.UserPersister;
import usw.suwiki.domain.viewexam.ViewExam;
import usw.suwiki.domain.viewexam.ViewExamRepository;

@Component
@RequiredArgsConstructor
public class ViewExamPersister {

  private final ViewExamRepository viewExamRepository;
  private final UserPersister userPersister;
  private final LecturePersister lecturePersister;

  public ViewExamBuilder builder() {
    return new ViewExamBuilder();
  }

  public final class ViewExamBuilder {

    private Long userId;
    private Long lectureId;

    public ViewExamBuilder setUserId(Long userId) {
      this.userId = userId;
      return this;
    }

    public ViewExamBuilder setLectureId(Long lectureId) {
      this.lectureId = lectureId;
      return this;
    }

    public ViewExam save() {
      ViewExam viewExam = new ViewExam(
        userId == null ? userPersister.builder().save().getId() : userId,
        lectureId == null ? lecturePersister.builder().save().getId() : lectureId
      );

      viewExamRepository.save(viewExam);
      return viewExam;
    }

  }
}
