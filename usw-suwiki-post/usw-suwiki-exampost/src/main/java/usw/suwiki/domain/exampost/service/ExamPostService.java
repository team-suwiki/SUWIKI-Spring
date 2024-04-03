package usw.suwiki.domain.exampost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExamPostException;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostQueryRepository;
import usw.suwiki.domain.exampost.ExamPostRepository;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.domain.report.model.Report;
import usw.suwiki.domain.report.service.ReportService;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.service.UserCRUDService;
import usw.suwiki.domain.viewexam.ViewExam;
import usw.suwiki.domain.viewexam.ViewExamQueryRepository;
import usw.suwiki.domain.viewexam.dto.ViewExamResponse;
import usw.suwiki.domain.viewexam.service.ViewExamCRUDService;

import java.util.List;

import static usw.suwiki.core.exception.ExceptionType.EXAM_POST_ALREADY_PURCHASE;
import static usw.suwiki.core.exception.ExceptionType.EXAM_POST_NOT_FOUND;
import static usw.suwiki.core.exception.ExceptionType.POSTS_WRITE_OVERLAP;
import static usw.suwiki.domain.exampost.dto.ExamPostResponse.Detail;
import static usw.suwiki.domain.exampost.dto.ExamPostResponse.Details;
import static usw.suwiki.domain.exampost.dto.ExamPostResponse.MyPost;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamPostService {
  private static final int PAGE_LIMIT = 10; // todo: hide when query repository testable

  private final ExamPostRepository examPostRepository;
  private final ExamPostQueryRepository examPostQueryRepository;

  private final LectureService lectureService;
  private final UserCRUDService userCRUDService;
  private final ReportService reportService;

  private final ViewExamCRUDService viewExamCRUDService;
  private final ViewExamQueryRepository viewExamQueryRepository;

  public boolean isAlreadyPurchased(Long userId, Long lectureId) {
    return viewExamCRUDService.isExist(userId, lectureId);
  }

  public boolean isAlreadyWritten(Long userId, Long lectureId) {
    return examPostRepository.existsByUserIdxAndLectureInfo_LectureId(userId, lectureId);
  }

  public List<ViewExamResponse.PurchaseHistory> loadPurchasedHistories(Long userId) {
    return viewExamQueryRepository.loadPurchasedHistoriesByUserId(userId);
  }

  public Details loadAllExamPosts(Long userId, Long lectureId, PageOption option) {
    boolean isWritten = isAlreadyWritten(userId, lectureId);

    List<Detail> data = examPostRepository.findAllByLectureIdAndPageOption(lectureId, option.getOffset(), PAGE_LIMIT).stream()
      .map(ExamPostMapper::toDetail)
      .toList(); // todo: to query repository

    Details response = data.isEmpty() ? Details.noData(isWritten) : Details.withData(data, isWritten);

    if (!isAlreadyPurchased(userId, lectureId)) {
      response.noAccess();
    }

    return response;
  }

  public List<MyPost> loadAllMyExamPosts(PageOption option, Long userId) {
    return examPostQueryRepository.findAllByUserIdAndPageOption(userId, option.getOffset());
  }

  public void report(Long reportingUserId, Long examId) {
    ExamPost examPost = loadExamPostOrThrow(examId);
    Long reportedUserId = examPost.getUserIdx();

    Report report = Report.exam(examId, reportedUserId, reportingUserId, examPost.getContent(), examPost.getLectureName(), examPost.getProfessor());
    reportService.reportExamPost(report);
  }

  @Transactional
  public void write(Long userId, Long lectureId, ExamPostRequest.Create request) {
    if (isAlreadyWritten(userId, lectureId)) {
      throw new AccountException(POSTS_WRITE_OVERLAP);
    }

    lectureService.findLectureById(lectureId);
    User user = userCRUDService.loadUserFromUserIdx(userId);

    ExamPost examPost = ExamPostMapper.toEntity(userId, lectureId, request);
    examPostRepository.save(examPost);

    user.writeExamPost();
  }

  @Transactional
  public void purchaseExamPost(Long userId, Long lectureId) {
    if (isAlreadyPurchased(userId, lectureId)) {
      throw new ExamPostException(EXAM_POST_ALREADY_PURCHASE);
    }

    lectureService.findLectureById(lectureId);
    User user = userCRUDService.loadUserFromUserIdx(userId);

    viewExamCRUDService.save(new ViewExam(userId, lectureId));
    user.purchaseExamPost();
  }

  @Transactional
  public void update(Long examId, ExamPostRequest.Update request) {
    ExamPost examPost = loadExamPostOrThrow(examId);
    examPost.update(request.getContent(), request.getSelectedSemester(), ExamPostMapper.toExamDetail(request));
  }

  @Transactional
  public void deleteExamPost(Long userIdx, Long examId) {
    ExamPost examPost = loadExamPostOrThrow(examId);

    User user = userCRUDService.loadUserFromUserIdx(userIdx);
    examPostRepository.delete(examPost);

    user.deleteExamPost();
  }

  private ExamPost loadExamPostOrThrow(Long examId) {
    return examPostRepository.findById(examId)
      .orElseThrow(() -> new ExamPostException(EXAM_POST_NOT_FOUND));
  }
}
