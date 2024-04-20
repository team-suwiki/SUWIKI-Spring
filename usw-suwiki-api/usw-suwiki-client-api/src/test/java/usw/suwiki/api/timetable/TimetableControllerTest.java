package usw.suwiki.api.timetable;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.common.test.Table;
import usw.suwiki.common.test.Tag;
import usw.suwiki.common.test.support.AcceptanceTestSupport;
import usw.suwiki.common.test.support.Uri;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.lecture.timetable.TimetableRepository;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.common.test.Table.TIMETABLE;
import static usw.suwiki.common.test.Table.TIMETABLE_CELLS;
import static usw.suwiki.common.test.Table.USERS;
import static usw.suwiki.common.test.extension.AssertExtension.expectExceptionJsonPath;
import static usw.suwiki.core.exception.ExceptionType.INVALID_TIMETABLE_SEMESTER;
import static usw.suwiki.core.exception.ExceptionType.INVALID_TOKEN;
import static usw.suwiki.core.exception.ExceptionType.NOT_AN_AUTHOR;
import static usw.suwiki.core.exception.ExceptionType.PARAMETER_VALIDATION_FAIL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerTest extends AcceptanceTestSupport {
  @Autowired
  private TimetableRepository timetableRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenAgent tokenAgent;

  private User user;
  private Claim claim;
  private String accessToken;

  @BeforeEach
  void setup() {
    user = userRepository.save(User.init("loginId", "password", "test@suwiki.kr"));
    claim = new UserClaim(user.getLoginId(), user.getRole().name(), user.getRestricted());

    accessToken = tokenAgent.createAccessToken(user.getId(), claim);
  }

  @Override
  protected Set<Table> targetTables() {
    return Set.of(USERS, TIMETABLE, TIMETABLE_CELLS);
  }

  @AfterEach
  @Override
  protected void clean() {
    super.databaseCleaner.clean(this.targetTables());
  }

  @Nested
  @DisplayName("시간표 생성 테스트")
  class CreateTimetableTest {
    private final String endpoint = "/timetables";

    @Test
    @DisplayName("시간표 생성 성공")
    void create_Success_WithoutException() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "first", "2024 1학기 시간표");

      // when
      var result = post(Uri.of(endpoint), accessToken, request);

      // then
      result.andExpectAll(
        status().isCreated(),
        jsonPath("$.data.success").value(true)
      );

      var timetables = timetableRepository.findAllByUserId(user.getId());
      assertAll(
        () -> assertThat(timetables.get(0)).isNotNull(),
        () -> assertThat(timetables.get(0).getName()).isEqualTo(request.getName()),
        () -> assertThat(timetables.get(0).getSemester()).isEqualTo(request.getSemester().toUpperCase()),
        () -> assertThat(timetables.get(0).getYear()).isEqualTo(request.getYear())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-timetable")
          .summary("[토큰 필요] 시간표 생성 API")
          .description("시간표를 생성하는 API 입니다. semester에는 [\"FIRST\", \"SECOND\", \"SUMMER\", \"WINTER\"] 만 입력 가능하니다.")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @CsvSource({
      "2019, 1학기 시간표",
      "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
    })
    @DisplayName("시간표 생성 실패 - 잘못된 연도, 긴 이름 입력")
    void create_Fail_ByInvalidRequests(int year, String name) throws Exception {
      // given
      var request = new TimetableRequest.Description(year, "first", name);

      // when
      var result = post(Uri.of(endpoint), accessToken, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, PARAMETER_VALIDATION_FAIL)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-timetable-fail-bad-request")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"one", "third", "spring", "hello"})
    @DisplayName("시간표 생성 실패 - 잘못된 학기 입력")
    void create_Fail_ByWrongSemesterEntered(String semester) throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, semester, "시간표입니다.");

      // when
      var result = post(Uri.of(endpoint), accessToken, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, INVALID_TIMETABLE_SEMESTER)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-timetable-fail-wrong-semester")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    @DisplayName("시간표 생성 실패 - 잘못된 토큰")
    void create_Fail_ByInvalidToken() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "first", "2024 1학기 시간표");

      // when
      var result = post(Uri.of(endpoint), INVALID_ACCESS_TOKEN, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, INVALID_TOKEN)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("create-timetable-fail-invalid-token")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  @DisplayName("시간표 수정 테스트")
  class UpdateTimetableTest {
    private final String endpoint = "/timetables/{timetableId}";

    private Timetable timetable;

    @BeforeEach
    void setup() {
      timetable = timetableRepository.save(new Timetable(user.getId(), "이전 시간표", 2023, "first"));
    }

    @Test
    @DisplayName("시간표 수정 성공")
    void update_Success_WithoutException() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), accessToken, request);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.success").value(true)
      );

      var timetables = timetableRepository.findAllByUserId(user.getId());
      assertAll(
        () -> assertThat(timetables.get(0)).isNotNull(),
        () -> assertThat(timetables.get(0).getName()).isNotEqualTo(timetable.getName()),
        () -> assertThat(timetables.get(0).getYear()).isNotEqualTo(timetable.getYear()),
        () -> assertThat(timetables.get(0).getSemester()).isNotEqualTo(timetable.getSemester())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-success")
          .summary("[토큰 필요] 시간표 수정 API")
          .description("시간표 수정 API 입니다. semester에는 [\"FIRST\", \"SECOND\", \"SUMMER\", \"WINTER\"] 만 입력 가능하니다.")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    @DisplayName("시간표 수정 실패 - 작성자가 아님")
    void update_Fail_ByNotAuthor() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");

      var anotherUserToken = tokenAgent.createAccessToken(2L, claim);

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), anotherUserToken, request);

      // then
      result.andExpectAll(
        status().is4xxClientError(),
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-fail-not-author")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @CsvSource({
      "2019, 1학기 시간표",
      "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
    })
    @DisplayName("시간표 수정 실패 - 잘못된 연도, 긴 이름 입력")
    void update_Fail_ByInvalidRequests(int year, String name) throws Exception {
      // given
      var request = new TimetableRequest.Description(year, "first", name);

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), accessToken, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, PARAMETER_VALIDATION_FAIL)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-fail-bad-request")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"one", "third", "spring", "hello"})
    @DisplayName("시간표 수정 실패 - 잘못된 학기 입력")
    void update_Fail_ByWrongSemesterEntered(String semester) throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, semester, "시간표입니다.");

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), accessToken, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, INVALID_TIMETABLE_SEMESTER)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-fail-wrong-semester")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    @DisplayName("시간표 수정 실패 - 잘못된 토큰")
    void update_Fail_ByInvalidToken() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), INVALID_ACCESS_TOKEN, request);

      // then
      result.andExpectAll(
        status().isBadRequest(),
        expectExceptionJsonPath(result, INVALID_TOKEN)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-fail-invalid-token")
          .tag(Tag.TIME_TABLE)
          .result(result)
          .generateDocs()
      );
    }
  }
}
