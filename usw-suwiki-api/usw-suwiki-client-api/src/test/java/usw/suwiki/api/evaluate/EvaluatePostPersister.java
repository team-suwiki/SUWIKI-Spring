package usw.suwiki.api.evaluate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.api.lecture.LecturePersister;
import usw.suwiki.api.user.UserPersister;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.EvaluatePostRepository;
import usw.suwiki.domain.evaluatepost.LectureInfo;
import usw.suwiki.domain.evaluatepost.LectureRating;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.model.Evaluation;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;

@Component
@RequiredArgsConstructor
public class EvaluatePostPersister {

  private final LecturePersister lecturePersister;
  private final UserPersister userPersister;
  private final EvaluatePostRepository evaluatePostRepository;
  private final UserRepository userRepository;
  private final LectureRepository lectureRepository;

  public EvaluatePostBuilder builder() {
    return new EvaluatePostBuilder();
  }

  public final class EvaluatePostBuilder {

    private Long userIdx;
    private String content;
    private LectureInfo lectureInfo;
    private LectureRating lectureRating;

    public EvaluatePostBuilder setUserIdx(Long userIdx) {
      this.userIdx = userIdx;
      return this;
    }

    public EvaluatePostBuilder setContent(String content) {
      this.content = content;
      return this;
    }

    public EvaluatePostBuilder setLectureInfo(LectureInfo lectureInfo) {
      this.lectureInfo = lectureInfo;
      return this;
    }

    public EvaluatePostBuilder setLectureInfo(Lecture lecture) {
      this.lectureInfo = new LectureInfo(
        lecture.getId(),
        lecture.getName(),
        lecture.getSemester(),
        lecture.getProfessor()
      );
      return this;
    }

    public EvaluatePostBuilder setLectureRating(LectureRating lectureRating) {
      this.lectureRating = lectureRating;
      return this;
    }

    public EvaluatePost save() {
      EvaluatePost evaluatePost = EvaluatePost.builder()
        .userIdx(userIdx == null ? userPersister.builder().save().getId() : userIdx)
        .content(content == null ? "강의 평가 내용" : content)
        .lectureInfo(lectureInfo == null ? initLectureInfo(lectureRating) : lectureInfo)
        .lectureRating(lectureRating == null ?
                new LectureRating(1, 1, 1, 1, 1,1) :
                lectureRating)
        .build();

      evaluateLecture(evaluatePost.getLectureId(), evaluatePost.getLectureRating());
      addPointToUser(evaluatePost.getUserIdx());

      return evaluatePostRepository.save(evaluatePost);
    }

    private void evaluateLecture(Long lectureId, LectureRating lectureRating) {
      Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new IllegalArgumentException("(evaluatePostPersister) 강의를 찾을 수 없습니다."));
      lecture.evaluate(toEvaluatedData(lectureRating));
    }

    private LectureInfo initLectureInfo(LectureRating lectureRating) {
      Lecture lecture = lecturePersister.builder().save();
      if (lectureRating == null) {
        lecture.evaluate(new Evaluation(1,1,1,1,1,1,1));
      }
      else {
        lecture.evaluate(toEvaluatedData(lectureRating));
      }
      return new LectureInfo(lecture.getId(), lecture.getName(),
              lecture.getSemester(), lecture.getProfessor());
    }

    private void addPointToUser(Long userIdx) {
      User user = userRepository.findById(userIdx)
              .orElseThrow(() -> new IllegalArgumentException("(evaluatePostPersister) 유저를 찾을 수 없습니다."));
      user.writeEvaluatePost();
    }

    public Evaluation toEvaluatedData(LectureRating lectureRating) {
      return new Evaluation(
        lectureRating.getTotalAvg(),
        lectureRating.getSatisfaction(),
        lectureRating.getHoney(),
        lectureRating.getLearning(),
        lectureRating.getTeam(),
        lectureRating.getDifficulty(),
        lectureRating.getHomework()
      );
    }
  }
}
