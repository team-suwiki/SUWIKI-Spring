package usw.suwiki.api.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.api.lecture.LecturePersister;
import usw.suwiki.api.user.UserPersister;
import usw.suwiki.domain.exampost.ExamDetail;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostRepository;
import usw.suwiki.domain.exampost.LectureInfo;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;

@Component
@RequiredArgsConstructor
public class ExamPostPersister {

  private final UserPersister userPersister;
  private final LecturePersister lecturePersister;
  private final UserRepository userRepository;
  private final ExamPostRepository examPostRepository;

  public ExamPostBuilder builder() {
    return new ExamPostBuilder();
  }

  public final class ExamPostBuilder {

    private Long userIdx;
    private String content;
    private LectureInfo lectureInfo;
    private ExamDetail examDetail;

    public ExamPostBuilder setUserIdx(Long userIdx) {
      this.userIdx = userIdx;
      return this;
    }

    public ExamPostBuilder setContent(String content) {
      this.content = content;
      return this;
    }

    public ExamPostBuilder setLectureInfo(LectureInfo lectureInfo) {
      this.lectureInfo = lectureInfo;
      return this;
    }

    public ExamPostBuilder setLectureInfo(Lecture lecture) {
      this.lectureInfo = new LectureInfo(
        lecture.getId(),
        lecture.getName(),
        lecture.getSemester(),
        lecture.getProfessor()
      );
      return this;
    }

    public ExamPostBuilder setExamDetail(ExamDetail examDetail) {
      this.examDetail = examDetail;
      return this;
    }

    public ExamPost save() {
      ExamPost examPost = ExamPost.builder()
        .userIdx((userIdx == null) ? userPersister.builder().save().getId() : userIdx)
        .content(content == null ? "시험 평가" : content)
        .lectureInfo(lectureInfo == null ? initLectureInfo() : lectureInfo)
        .examDetail(examDetail == null ? initExamDetail() : examDetail)
        .build();
      addPointToUser(examPost.getUserIdx());
      return examPostRepository.save(examPost);
    }

    private void addPointToUser(Long userIdx) {
      User user = userRepository.findById(userIdx)
        .orElseThrow(() -> new IllegalArgumentException("(examPostPersister) 유저를 찾을 수 없습니다."));
      user.writeExamPost();
    }

    private ExamDetail initExamDetail() {
      return new ExamDetail("중간고사", "PPT", "어려움");
    }

    private LectureInfo initLectureInfo() {
      Lecture lecture = lecturePersister.builder().save();
      return new LectureInfo(lecture.getId(), lecture.getName(),
              lecture.getSemester(), lecture.getProfessor());
    }

  }
}
