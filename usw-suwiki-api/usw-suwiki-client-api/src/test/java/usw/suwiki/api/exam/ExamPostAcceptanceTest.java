package usw.suwiki.api.exam;

import io.github.hejow.restdocs.document.RestDocument;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import usw.suwiki.api.lecture.LecturePersister;
import usw.suwiki.api.user.UserPersister;
import usw.suwiki.common.test.annotation.AcceptanceTest;
import usw.suwiki.common.test.support.AcceptanceTestSupport;
import usw.suwiki.common.test.support.ResponseValidator;
import usw.suwiki.common.test.support.Uri;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.exampost.ExamPost;
import usw.suwiki.domain.exampost.ExamPostRepository;
import usw.suwiki.domain.exampost.dto.ExamPostRequest;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.model.UserClaim;
import usw.suwiki.domain.viewexam.ViewExam;
import usw.suwiki.domain.viewexam.ViewExamRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.common.test.Tag.EXAM_POST;
import static usw.suwiki.common.test.Tag.NOTICE;
import static usw.suwiki.common.test.extension.AssertExtension.expectExceptionJsonPath;
import static usw.suwiki.common.test.support.Pair.parameter;
import static usw.suwiki.core.exception.ExceptionType.*;

@AcceptanceTest
@Transactional
public class ExamPostAcceptanceTest extends AcceptanceTestSupport {

  @Autowired
  private ExamPostPersister examPostPersister;
  @Autowired
  private ExamPostRepository examPostRepository;
  @Autowired
  private TokenAgent tokenAgent;
  @Autowired
  private UserPersister userPersister;
  @Autowired
  private LecturePersister lecturePersister;
  @Autowired
  private ViewExamRepository viewExamRepository;
  @Autowired
  private ViewExamPersister viewExamPersister;

  private User user;
  private Claim claim;
  private String accessToken;
  @Autowired
  private LectureRepository lectureRepository;

  @BeforeEach
  public void setup() {
    user = userPersister.builder().save();
    claim = new UserClaim("loginId", user.getRole().name(), user.getRestricted());  //Admin 설정
    accessToken = tokenAgent.createAccessToken(user.getId(), claim);
  }

  @Nested
  class 시험_평가_작성_테스트 {
    private final int WRITE_EXAM_POST_BONUS_POINT = 20;

    private final String endpoint = "/exam-posts";
    private final String paramKey = "lectureId";

    @Test
    void 시험_평가_작성_성공() throws Exception {
      // expected
      var identifier = "create-exam-post";
      var summary = "[토큰 필요] 시험 평가 생성 API";
      var description = "시험 평가를 생성하는 API 입니다.";
      var tag = EXAM_POST;
      var expectedResults = "success";

      // given
      var lecture = lecturePersister.builder().save();
      var request = new ExamPostRequest.Create(
        lecture.getName(), lecture.getSemester(), lecture.getProfessor(),
        "중간고사", "PPT", "어려움", "어렵네요");
      var beforePoint = user.getPoint();
      var beforeWrittenExamCount = user.getWrittenExam();

      // when
      var result = post(Uri.of(endpoint), accessToken, request, parameter(paramKey, lecture.getId()));

      // then
      var evaluatePosts = examPostRepository.findAllByUserIdx(user.getId());

      assertAll(
        () -> assertThat(evaluatePosts.get(0)).isNotNull(),
        () -> assertThat(evaluatePosts.get(0).getUserIdx()).isEqualTo(user.getId()),
        () -> assertThat(evaluatePosts.get(0).getExamDifficulty()).isEqualTo(request.getExamDifficulty()),
        () -> assertThat(evaluatePosts.get(0).getExamInfo()).isEqualTo(request.getExamInfo()),
        () -> assertThat(evaluatePosts.get(0).getExamType()).isEqualTo(request.getExamType()),
        () -> assertThat(evaluatePosts.get(0).getContent()).isEqualTo(request.getContent()),
        () -> assertThat(evaluatePosts.get(0).getLectureId()).isEqualTo(lecture.getId()),
        () -> assertThat(evaluatePosts.get(0).getLectureName()).isEqualTo(request.getLectureName()),
        () -> assertThat(evaluatePosts.get(0).getSelectedSemester()).isEqualTo(request.getSelectedSemester()),
        () -> assertThat(evaluatePosts.get(0).getProfessor()).isEqualTo(request.getProfessor()),
        () -> assertThat(user.getPoint()).isEqualTo(beforePoint + WRITE_EXAM_POST_BONUS_POINT),
        () -> assertThat(user.getWrittenExam()).isEqualTo(beforeWrittenExamCount + 1)
      );

      // result validation
      ResponseValidator.validateNonJSONResponse(result, status().isOk(), expectedResults);

      // Non DOCS
    }

    @Test
    void 시험_평가_작성_실패_중복_작성() throws Exception {
      // given
      var examPost = examPostPersister.builder().setUserIdx(user.getId()).save();
      var request = new ExamPostRequest.Create(
        examPost.getLectureName(), examPost.getSelectedSemester(), examPost.getProfessor(),
        "중간고사", "PPT", "어려움", "어렵네요");

      // when
      var result = post(Uri.of(endpoint), accessToken, request, parameter(paramKey, examPost.getLectureId()));

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, POSTS_WRITE_OVERLAP)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-exam-post-fail-already-written")
          .tag(NOTICE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시험_평가_생성_실패_존재하지_않는_강의() throws Exception {
      // given
      var lectureId = 0L;

      var request = new ExamPostRequest.Create(
        "강의명", "2021-1", "교수",
        "중간고사", "PPT", "어려움", "어렵네요");

      // when
      var result = post(Uri.of(endpoint), accessToken, request, parameter(paramKey, lectureId));

      // then
      result.andExpectAll(
        status().isNotFound(),
        expectExceptionJsonPath(result, LECTURE_NOT_FOUND)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-exam-post-fail-wrong-parameter-lecture-id")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시험_평가_수정_테스트 {
    private final String endpoint = "/exam-posts";
    private final String paramKey = "examIdx";

    @Test
    void 시험_평가_수정_성공() throws Exception {
      // expected
      var identifier = "update-exam-post";
      var summary = "[토큰 필요] 시험 평가 수정 API";
      var description = "시험 평가를 수정하는 API 입니다."; // todo
      var tag = EXAM_POST;
      var expectedResults = "success";

      // given
      var examPost = examPostPersister.builder().setUserIdx(user.getId()).save();
      var request = new ExamPostRequest.Update("2024-1", "기말고사",
        "과제", "쉬움", "쉽네요");

      // when
      var result = put(Uri.of(endpoint), accessToken, request, parameter(paramKey, examPost.getId()));

      // then
      var evaluatePosts = examPostRepository.findAllByUserIdx(user.getId());

      assertAll(
        () -> assertThat(evaluatePosts.get(0)).isNotNull(),
        () -> assertThat(evaluatePosts.get(0).getExamDifficulty()).isEqualTo(request.getExamDifficulty()),
        () -> assertThat(evaluatePosts.get(0).getExamInfo()).isEqualTo(request.getExamInfo()),
        () -> assertThat(evaluatePosts.get(0).getExamType()).isEqualTo(request.getExamType()),
        () -> assertThat(evaluatePosts.get(0).getContent()).isEqualTo(request.getContent()),
        () -> assertThat(evaluatePosts.get(0).getSelectedSemester()).isEqualTo(request.getSelectedSemester())
      );

      // result validation
      ResponseValidator.validateNonJSONResponse(result, status().isOk(), expectedResults);

      // Non DOCS
    }

    @Test
    void 시험_평가_수정_실패_존재하지_않는_파라미터() throws Exception {
      // given
      Long examIdx = 0L;

      var request = new ExamPostRequest.Update("2024-1", "기말고사", "과제",
        "쉬움", "쉽네요");

      // when
      var result = put(Uri.of(endpoint), accessToken, request, parameter(paramKey, examIdx));

      // then
      result.andExpectAll(
        status().isNotFound(),
        expectExceptionJsonPath(result, EXAM_POST_NOT_FOUND)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-exam-post-fail-wrong-parameter-exam-id")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시험_평가_삭제_테스트 {
    private final String endpoint = "/exam-posts";
    private final String paramKey = "examIdx";

    @Test
    void 시험_평가_삭제_성공() throws Exception {
      // expected
      var identifier = "delete-exam-post";
      var summary = "[토큰 필요] 시험 평가 삭제 API";
      var description = "시험 평가를 삭제하는 API 입니다.";
      var tag = EXAM_POST;
      var expectedResults = "success";

      // given
      var examPost = examPostPersister.builder().setUserIdx(user.getId()).save();
      addUserPoint(user);

      // when
      var result = delete(Uri.of(endpoint), accessToken, null, parameter(paramKey, examPost.getId()));

      // then
      var evaluatePosts = examPostRepository.findAllByUserIdx(user.getId());

      assertAll(
        () -> assertThat(evaluatePosts.size()).isZero()
      );

      // result validation
      ResponseValidator.validateNonJSONResponse(result, status().isOk(), expectedResults);

      // Non DOCS
    }

    @Test
    void 시험_평가_삭제_실패_유저_포인트_부족() throws Exception {
      // given
      var examPost = examPostPersister.builder().setUserIdx(user.getId()).save();

      // when
      var result = delete(Uri.of(endpoint), accessToken, null, parameter(paramKey, examPost.getId()));

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, USER_POINT_LACK)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-exam-post-fail-lack-user-point")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시험_평가_수정_실패_존재하지_않는_파라미터() throws Exception {
      // given
      var examIdx = 0L;

      // when
      var result = delete(Uri.of(endpoint), accessToken, null, parameter(paramKey, examIdx));

      // then
      result.andExpectAll(
        status().isNotFound(),
        expectExceptionJsonPath(result, EXAM_POST_NOT_FOUND)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-exam-post-fail-wrong-parameter-exam-id")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시험_평가_구매_테스트 {
    private final int PURCHASE_POINT = 20;

    private final String endpoint = "/exam-posts/purchase";
    private final String paramKey = "lectureId";

    @Test
    void 시험_평가_구매_성공() throws Exception {
      // expected
      var identifier = "purchase-exam-post";
      var summary = "[토큰 필요] 시험 평가 구매 API";
      var description = "시험 평가를 구매하는 API 입니다.";
      var tag = EXAM_POST;
      var expectedResults = "success";

      // given
      var lecture = lecturePersister.builder().save();
      addUserPoint(user);
      var beforePoint = user.getPoint();
      var beforeViewExamCount = user.getViewExamCount();

      // when
      var result = post(Uri.of(endpoint), accessToken, null, parameter(paramKey, lecture.getId()));

      // then
      var exists = viewExamRepository.validateIsExists(user.getId(), lecture.getId());

      assertAll(
        () -> assertThat(exists).isTrue(),
        () -> assertThat(user.getPoint()).isEqualTo(beforePoint- PURCHASE_POINT),
        () -> assertThat(user.getViewExamCount()).isEqualTo(beforeViewExamCount+1)
      );

      // result validation
      ResponseValidator.validateNonJSONResponse(result, status().isOk(), expectedResults);

      // Non DOCS
    }

    @Test
    void 시험_평가_구매_실패_유저_포인트_부족() throws Exception {
      // given
      var lecture = lecturePersister.builder().save();

      // when
      var result = post(Uri.of(endpoint), accessToken, null, parameter(paramKey, lecture.getId()));

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, USER_POINT_LACK)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("purchase-exam-post-fail-lack-user-point")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시험_평가_구매_실패_중복_구매() throws Exception {
      // given
      var viewExam = viewExamPersister.builder().setUserId(user.getId()).save();

      // when
      var result = post(Uri.of(endpoint), accessToken, null, parameter(paramKey, viewExam.getLectureId()));

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, EXAM_POST_ALREADY_PURCHASE)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("purchase-exam-post-fail-already-purchase")
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 내가_쓴_시험_평가_조회_테스트 {
    private final int DEFAULT_SIZE = 10;
    private final String endpoint = "/exam-posts/written";
    private final String paramKey = "page";

    @Test
    void 내가_쓴_시험_평가_조회_성공() throws Exception {
      // given
      var totalPage = 3;
      var requestPage = 1;
      var size = DEFAULT_SIZE * totalPage;
      List<ExamPost> examPosts = new ArrayList<>();
      for (int cnt = 0; cnt < size; cnt++) {
        var examPost = examPostPersister.builder().setUserIdx(user.getId()).save();
        examPosts.add(examPost);
      }

      // when
      var result = get(Uri.of(endpoint), accessToken, parameter(paramKey, requestPage));

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.length()").value(10),
        jsonPath("$.data.[0].id").value(examPosts.get(0).getId()),
        jsonPath("$.data.[0].content").value(examPosts.get(0).getContent()),
        jsonPath("$.data.[0].lectureName").value(examPosts.get(0).getLectureName()),
        jsonPath("$.data.[0].selectedSemester").value(examPosts.get(0).getSelectedSemester()),
        jsonPath("$.data.[0].professor").value(getLecture(examPosts.get(0).getLectureId()).getProfessor()),
        jsonPath("$.data.[0].majorType").value(getLecture(examPosts.get(0).getLectureId()).getMajorType()),
        jsonPath("$.data.[0].semesterList").value(getLecture(examPosts.get(0).getLectureId()).getSemester()),
        jsonPath("$.data.[0].examType").value(examPosts.get(0).getExamType()),
        jsonPath("$.data.[0].examInfo").value(examPosts.get(0).getExamInfo()),
        jsonPath("$.data.[0].examDifficulty").value(examPosts.get(0).getExamDifficulty())
      );

      // docs
//      result.andDo(
//        RestDocument.builder()
//          .identifier("get-exam_post-written-by-me-success")
//          .summary("내가 쓴 시험 평가 조회 API")
//          .description("")
//          .tag(EXAM_POST)
//          .result(result)
//          .generateDocs()
//      );
    }
  }

  @Nested
  class 시험_평가_구매_이력_조회_테스트 {
    private final String endpoint = "/exam-posts/purchase";

    @Test
    void 시험_평가_구매_이력_조회_성공() throws Exception {
      // given
      ViewExam viewExam = viewExamPersister.builder().setUserId(user.getId()).save();

      // when
      var result = get(Uri.of(endpoint), accessToken);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.[0].id").value(viewExam.getId()),
        jsonPath("$.data.[0].professor").value(getLecture(viewExam.getLectureId()).getProfessor()),
        jsonPath("$.data.[0].lectureName").value(getLecture(viewExam.getLectureId()).getName()),
        jsonPath("$.data.[0].majorType").value(getLecture(viewExam.getLectureId()).getMajorType()),
        jsonPath("$.data.[0].createDate").value(viewExam.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
      );

      // docs
//      result.andDo(
//        RestDocument.builder()
//          .identifier("get-exam_post-purchase-history-success")
//          .summary("시험 평가 구매 이력 조회 API")
//          .description("")
//          .tag(EXAM_POST)
//          .result(result)
//          .generateDocs()
//      );
    }

  }

  @Nested
  class 시험_평가_리스트_조회_테스트 {
    private final int DEFAULT_SIZE = 10;
    private int totalPage = 3;
    private int requestPage = 1;
    private int size = DEFAULT_SIZE * totalPage;

    private final String endpoint = "/exam-posts";
    private final String pageParam = "page";
    private final String lectureIdParam = "lectureId";

    @Test
    void 시험_평가_리스트_조회_성공_시험_평가_미작성_유저() throws Exception {
      // given
      List<ExamPost> examPosts = new ArrayList<>();
      var lecture = lecturePersister.builder().save();
      viewExamPersister.builder().setUserId(user.getId()).setLectureId(lecture.getId()).save();
      for (int cnt = 0; cnt < size; cnt++) {
        var examPost = examPostPersister.builder().setLectureInfo(lecture).setContent("내용" + cnt).save();
        examPosts.add(examPost);
      }

      // when
      var result = get(Uri.of(endpoint), accessToken,
        parameter(pageParam, requestPage),
        parameter(lectureIdParam, lecture.getId())
      );

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.size()").value(DEFAULT_SIZE),
        jsonPath("$.data.[0].id").value(examPosts.get(0).getId()),
        jsonPath("$.data.[0].content").value(examPosts.get(0).getContent()),
        jsonPath("$.data.[0].selectedSemester").value(examPosts.get(0).getSelectedSemester()),
        jsonPath("$.data.[0].examType").value(examPosts.get(0).getExamType()),
        jsonPath("$.data.[0]..examInfo").value(examPosts.get(0).getExamInfo()),
        jsonPath("$.data.[0]..examDifficulty").value(examPosts.get(0).getExamDifficulty()),
        jsonPath("$.canRead").value(true),
        jsonPath("$.examDataExist").value(true),
        jsonPath("$.written").value(false)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("get-exam_posts-success")
          .summary("시험 평가 리스트 조회 API")
          .description("""
            파라미터는 다음과 같습니다.
            page : 정수,
            lectureId : 강의 ID
            
            """)
          .tag(EXAM_POST)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시험_평가_리스트_조회_성공_시험_평가_작성_유저() throws Exception {
      // given
      List<ExamPost> examPosts = new ArrayList<>();
      var lecture = lecturePersister.builder().save();
      viewExamPersister.builder().setUserId(user.getId()).setLectureId(lecture.getId()).save();
      for (int cnt = 0; cnt < size; cnt++) {
        var examPost = examPostPersister.builder().setLectureInfo(lecture).setContent("내용" + cnt).save();
        examPosts.add(examPost);
      }
      examPostPersister.builder().setUserIdx(user.getId()).setLectureInfo(lecture).save();

      // when
      var result = get(Uri.of(endpoint), accessToken,
        parameter(pageParam, requestPage),
        parameter(lectureIdParam, lecture.getId())
      );

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.size()").value(DEFAULT_SIZE),
        jsonPath("$.data.[0].id").value(examPosts.get(0).getId()),
        jsonPath("$.data.[0].content").value(examPosts.get(0).getContent()),
        jsonPath("$.data.[0].selectedSemester").value(examPosts.get(0).getSelectedSemester()),
        jsonPath("$.data.[0].examType").value(examPosts.get(0).getExamType()),
        jsonPath("$.data.[0]..examInfo").value(examPosts.get(0).getExamInfo()),
        jsonPath("$.data.[0]..examDifficulty").value(examPosts.get(0).getExamDifficulty()),
        jsonPath("$.canRead").value(true),
        jsonPath("$.examDataExist").value(true),
        jsonPath("$.written").value(true)
      );
    }

    @Test
    void 시험_평가_리스트_조회_성공_미구매_유저() throws Exception {
      // given
      List<ExamPost> examPosts = new ArrayList<>();
      var lecture = lecturePersister.builder().save();
      for (int cnt = 0; cnt < size; cnt++) {
        var examPost = examPostPersister.builder().setLectureInfo(lecture).setContent("내용" + cnt).save();
        examPosts.add(examPost);
      }

      // when
      var result = get(Uri.of(endpoint), accessToken,
        parameter(pageParam, requestPage),
        parameter(lectureIdParam, lecture.getId())
      );

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data").isEmpty(),
        jsonPath("$.canRead").value(false),
        jsonPath("$.examDataExist").value(true),
        jsonPath("$.written").value(false)
      );

      // docs
    }
  }

  private void addUserPoint(User user) { // 200포인트 증가
    for (int cnt = 0; cnt < 10; cnt++) {
      user.writeExamPost();
    }
  }

  private Lecture getLecture(Long lectureId) {
    return lectureRepository.findById(lectureId).orElseThrow(() -> new IllegalArgumentException("(ExamPostAcceptanceTest) 강의를 찾을 수 없습니다."));
  }
}
