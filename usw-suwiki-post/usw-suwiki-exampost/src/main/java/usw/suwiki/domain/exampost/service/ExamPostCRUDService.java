package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExamPostException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamPostCRUDService {
  private final ExamPostRepository examPostRepository;

  public ExamPost loadExamPostFromExamPostIdx(Long examIdx) {
    return examPostRepository.findById(examIdx)
      .orElseThrow(() -> new ExamPostException(ExceptionType.EXAM_POST_NOT_FOUND));
  }

  @Transactional
  public void deleteFromUserIdx(Long userIdx) {
    List<ExamPost> examPosts = examPostRepository.findAllByUserIdx(userIdx);
    examPostRepository.deleteAllInBatch(examPosts);
  }

  @Transactional
  public void delete(ExamPost examPost) {
    examPostRepository.delete(examPost);
  }
}
