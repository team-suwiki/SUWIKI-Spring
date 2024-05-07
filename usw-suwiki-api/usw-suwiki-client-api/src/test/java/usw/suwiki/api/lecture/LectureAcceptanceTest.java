package usw.suwiki.api.lecture;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import usw.suwiki.common.test.Tag;
import usw.suwiki.common.test.annotation.AcceptanceTest;
import usw.suwiki.common.test.support.AcceptanceTestSupport;
import usw.suwiki.common.test.support.Uri;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.schedule.LectureScheduleRepository;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.common.test.extension.AssertExtension.expectExceptionJsonPath;
import static usw.suwiki.common.test.support.Pair.parameter;
import static usw.suwiki.core.exception.ExceptionType.USER_RESTRICTED;

@AcceptanceTest
class LectureAcceptanceTest extends AcceptanceTestSupport {
  @Autowired
  private LectureRepository lectureRepository;
  @Autowired
  private LectureScheduleRepository lectureScheduleRepository;
  @Autowired
  private TokenAgent tokenAgent;

  @Nested
  class 강의_검색_테스트 {
    private final int DEFAULT_SIZE = 10;

    @Test
    void 강의_내림차순_검색_성공() throws Exception {
      // given
      var size = DEFAULT_SIZE * 2;
      var index = size - 1;

      var lectures = 강의_생성(size);

      // when
      var result = get(Uri.of("/lecture/search"),
        parameter("searchValue", "교수"),
        parameter("option", "modifiedDate"),
        parameter("page", 1),
        parameter("majorType", "전체")
      );

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.count").value(size),
        jsonPath("$.data.length()").value(DEFAULT_SIZE),
        jsonPath("$.data.[0].semesterList").value(lectures.get(index).getSemester()),
        jsonPath("$.data.[0].professor").value(lectures.get(index).getProfessor()),
        jsonPath("$.data.[0].lectureName").value(lectures.get(index).getName()),
        jsonPath("$.data.[0].majorType").value(lectures.get(index).getMajorType()),
        jsonPath("$.data.[0].lectureType").value(lectures.get(index).getType()),
        jsonPath("$.data.[0].lectureTotalAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureTotalAvg()),
        jsonPath("$.data.[0].lectureSatisfactionAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureSatisfactionAvg()),
        jsonPath("$.data.[0].lectureHoneyAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureHoneyAvg()),
        jsonPath("$.data.[0].lectureLearningAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureLearningAvg())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("search-lecture-success")
          .summary("강의 통합 검색 API")
          .description("""
            강의 통합 검색 API 입니다. 검색에 사용되는 값은 다음과 같습니다.
            searchValue : '교수 이름' or '강의 이름'
            option : 'modifiedDate', 'lectureSatisfactionAvg', 'lectureHoneyAvg', 'lectureLearningAvg' 중 택 1
            page : 정수,
            majorType : 전공
            기본 값은 최근 올라온 강의 순으로 10개를 가져옵니다.
            """)
          .tag(Tag.LECTURE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 강의_검색_커서_동작_성공() throws Exception {
      // given
      var page = 3;
      var size = DEFAULT_SIZE * page;
      var index = size - 1 - DEFAULT_SIZE * (page - 1);

      var lectures = 강의_생성(size);

      // when
      var result = get(Uri.of("/lecture/search"),
        parameter("searchValue", "교수"),
        parameter("option", "modifiedDate"),
        parameter("page", page),
        parameter("majorType", "전체")
      );

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.count").value(size),
        jsonPath("$.data.length()").value(DEFAULT_SIZE),
        jsonPath("$.data.[0].semesterList").value(lectures.get(index).getSemester()),
        jsonPath("$.data.[0].professor").value(lectures.get(index).getProfessor()),
        jsonPath("$.data.[0].lectureName").value(lectures.get(index).getName()),
        jsonPath("$.data.[0].majorType").value(lectures.get(index).getMajorType()),
        jsonPath("$.data.[0].lectureType").value(lectures.get(index).getType()),
        jsonPath("$.data.[0].lectureTotalAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureTotalAvg()),
        jsonPath("$.data.[0].lectureSatisfactionAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureSatisfactionAvg()),
        jsonPath("$.data.[0].lectureHoneyAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureHoneyAvg()),
        jsonPath("$.data.[0].lectureLearningAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureLearningAvg())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("search-lecture-cursor-success")
          .tag(Tag.LECTURE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Test
  void 메인_페이지_강의_조회_성공() throws Exception {
    // given
    var size = 10;
    var index = size - 1;

    var lectures = 강의_생성(size);

    // when
    var result = get(Uri.of("/lecture/all"),
      parameter("searchValue", "교수"),
      parameter("option", "modifiedDate"),
      parameter("page", 1),
      parameter("majorType", "전체")
    );

    // then
    result.andExpectAll(
      status().isOk(),
      jsonPath("$.count").value(size),
      jsonPath("$.data.length()").value(size),
      jsonPath("$.data.[0].semesterList").value(lectures.get(index).getSemester()),
      jsonPath("$.data.[0].professor").value(lectures.get(index).getProfessor()),
      jsonPath("$.data.[0].lectureName").value(lectures.get(index).getName()),
      jsonPath("$.data.[0].majorType").value(lectures.get(index).getMajorType()),
      jsonPath("$.data.[0].lectureType").value(lectures.get(index).getType()),
      jsonPath("$.data.[0].lectureTotalAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureTotalAvg()),
      jsonPath("$.data.[0].lectureSatisfactionAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureSatisfactionAvg()),
      jsonPath("$.data.[0].lectureHoneyAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureHoneyAvg()),
      jsonPath("$.data.[0].lectureLearningAvg").value(lectures.get(index).getLectureEvaluationInfo().getLectureLearningAvg())
    );

    // docs
    result.andDo(
      RestDocument.builder()
        .identifier("get-main-page-lecture-success")
        .summary("메인 페이지 강의 조회 API")
        .description("""
          메인 페이지에 사용되는 강의 조회 API 입니다. 검색에 사용되는 값은 다음과 같습니다.
          option : 'modifiedDate', 'lectureSatisfactionAvg', 'lectureHoneyAvg', 'lectureLearningAvg' 중 택 1
          page : 정수,
          majorType : 전공
          기본 값은 최근 올라온 강의 순으로 10개를 가져옵니다.
          """)
        .tag(Tag.LECTURE)
        .result(result)
        .generateDocs()
    );
  }

  @Nested
  class 강의_상세조회_테스트 {
    private final String endpoint = "/lecture";

    @Test
    void 강의_상세조회_성공() throws Exception {
      // given
      var accessToken = tokenAgent.createAccessToken(1L, new UserClaim("loginId", "USER", false));

      var lecture = 강의_생성();

      // when
      var result = get(Uri.of(endpoint), accessToken, parameter("lectureId", lecture.getId()));

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.id").value(lecture.getId()),
        jsonPath("$.data.semesterList").value(lecture.getSemester()),
        jsonPath("$.data.professor").value(lecture.getProfessor()),
        jsonPath("$.data.lectureType").value(lecture.getType()),
        jsonPath("$.data.lectureName").value(lecture.getName()),
        jsonPath("$.data.majorType").value(lecture.getMajorType()),
        jsonPath("$.data.lectureTotalAvg").value(lecture.getLectureEvaluationInfo().getLectureTotalAvg()),
        jsonPath("$.data.lectureSatisfactionAvg").value(lecture.getLectureEvaluationInfo().getLectureSatisfactionAvg()),
        jsonPath("$.data.lectureHoneyAvg").value(lecture.getLectureEvaluationInfo().getLectureHoneyAvg()),
        jsonPath("$.data.lectureLearningAvg").value(lecture.getLectureEvaluationInfo().getLectureLearningAvg()),
        jsonPath("$.data.lectureTeamAvg").value(lecture.getLectureEvaluationInfo().getLectureTeamAvg()),
        jsonPath("$.data.lectureDifficultyAvg").value(lecture.getLectureEvaluationInfo().getLectureDifficultyAvg()),
        jsonPath("$.data.lectureHomeworkAvg").value(lecture.getLectureEvaluationInfo().getLectureHomeworkAvg())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("get-lecture-success")
          .summary("[토큰 필요] 강의 조회 API")
          .description("강의 조회 API입니다. 토큰을 넣지 않는 경우 403 에러가 발생합니다.")
          .tag(Tag.LECTURE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 강의_상세조회_실패_제한된_유저() throws Exception {
      // given
      var accessToken = tokenAgent.createAccessToken(1L, new UserClaim("loginId", "USER", true));

      var lecture = 강의_생성();

      // when
      var result = get(Uri.of(endpoint), accessToken, parameter("lectureId", lecture.getId()));

      // then
      result.andExpectAll(
        status().isForbidden(),
        expectExceptionJsonPath(result, USER_RESTRICTED)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("get-lecture-fail-restricted-user")
          .tag(Tag.LECTURE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Test
  void 시간표_전용_강의_조회_성공() throws Exception {
    // given
    var lecture = 강의_생성();
    var lectureSchedule = 강의_일정_생성(lecture.getId());

    // when
    var result = get(Uri.of("/lecture/current/cells/search"));

    // then
    result.andExpectAll(
      status().isOk(),
      jsonPath("$.data.isLast").value(true),
      jsonPath("$.data.content[0].id").value(lecture.getId()),
      jsonPath("$.data.content[0].name").value(lecture.getName()),
      jsonPath("$.data.content[0].type").value(lecture.getType()),
      jsonPath("$.data.content[0].major").value(lecture.getMajorType()),
      jsonPath("$.data.content[0].professorName").value(lecture.getProfessor()),
      jsonPath("$.data.content[0].originalCellList.size()").value(lectureSchedule.getPlaceSchedule().split(",(?![^()]*\\))").length)
    );

    // docs
    result.andDo(
      RestDocument.builder()
        .identifier("get-lecture-schedule-success")
        .summary("시간표용 강의 정보 조회 API")
        .description("""
          시간표용 강의 정보 조회 API 입니다. 최근 학기를 기준으로 조회됩니다. 사용할 수 있는 QueryString 은 다음과 같습니다.
          cursorId : 마지막으로 조회한 식별자 (default : 0)
          size : 제한 없음 (default : 20)
          major : 전공
          keyword : 검색할 키워드, 강의 혹은 교수 이름으로 검색,
          grade : 학년 (1 ~ 4)
          """)
        .tag(Tag.LECTURE)
        .result(result)
        .generateDocs()
    );
  }

  private Lecture 강의_생성() {
    return lectureRepository.save(LectureFixture.one());
  }

  private List<Lecture> 강의_생성(int size) {
    return lectureRepository.saveAll(LectureFixture.list(size));
  }

  private LectureSchedule 강의_일정_생성(Long lectureId) {
    return lectureScheduleRepository.save(LectureScheduleFixture.one(lectureId));
  }
}
