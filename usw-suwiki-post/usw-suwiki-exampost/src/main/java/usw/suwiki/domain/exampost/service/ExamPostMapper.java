package usw.suwiki.domain.exampost.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.exampost.ExamDetail;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.LectureInfo;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.exampost.dto.ExamPostResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExamPostMapper {

  public static ExamPost toEntity(Long userId, Long lectureId, ExamPostRequest.Create request) {
    return ExamPost.builder()
      .userIdx(userId)
      .content(request.getContent())
      .lectureInfo(new LectureInfo(lectureId, request.getLectureName(), request.getSelectedSemester(), request.getProfessor()))
      .examDetail(new ExamDetail(request.getExamType(), request.getExamInfo(), request.getExamDifficulty()))
      .build();
  }

  public static ExamPostResponse.Detail toDetail(ExamPost examPost) {
    return new ExamPostResponse.Detail(
      examPost.getId(),
      examPost.getContent(),
      examPost.getSelectedSemester(),
      examPost.getExamType(),
      examPost.getExamInfo(),
      examPost.getExamDifficulty()
    );
  }

  public static ExamDetail toExamDetail(ExamPostRequest.Update request) {
    return new ExamDetail(request.getExamType(), request.getExamInfo(), request.getExamDifficulty());
  }
}
