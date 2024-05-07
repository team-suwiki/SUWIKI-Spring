package usw.suwiki.domain.lecture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureQueryRepository;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.model.Evaluation;
import usw.suwiki.domain.lecture.model.Lectures;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {
  private final LectureQueryRepository lectureQueryRepository;
  private final LectureRepository lectureRepository;

  public List<String> loadMajorTypes() {
    return lectureQueryRepository.findAllMajorTypes();
  }

  public Lecture findLectureById(Long id) {
    return lectureRepository.findById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }

  public LectureResponse.Detail loadLectureDetail(Long lectureId) {
    Lecture lecture = findLectureById(lectureId);
    return LectureMapper.toDetail(lecture);
  }

  public LectureResponse.Simples loadAllLecturesByKeyword(String keyword, LectureSearchOption option) {
    return toResponse(option.passMajorFiltering()
      ? lectureQueryRepository.findAllLecturesByOption(keyword, option)
      : lectureQueryRepository.findAllLecturesByMajorType(keyword, option)
    );
  }

  public LectureResponse.Simples loadAllLectures(LectureSearchOption option) {
    return toResponse(option.passMajorFiltering()
      ? lectureQueryRepository.findAllLecturesByOption(option)
      : lectureQueryRepository.findAllLecturesByMajorType(option)
    );
  }

  private LectureResponse.Simples toResponse(Lectures lectures) {
    return new LectureResponse.Simples(
      lectures.count(),
      lectures.content().stream().map(LectureMapper::toSimple).toList()
    );
  }

  @Transactional
  public void evaluate(Long lectureId, Evaluation evaluation) {
    Lecture lecture = loadLectureFromIdPessimisticLock(lectureId);
    lecture.evaluate(evaluation);
  }

  @Transactional
  public void updateEvaluation(Long lectureId, Evaluation current, Evaluation update) {
    Lecture lecture = loadLectureFromIdPessimisticLock(lectureId);
    lecture.updateEvaluation(current, update);
  }

  private Lecture loadLectureFromIdPessimisticLock(Long id) {
    return lectureRepository.findForUpdateById(id)
      .orElseThrow(() -> new LectureException(ExceptionType.LECTURE_NOT_FOUND));
  }
}
