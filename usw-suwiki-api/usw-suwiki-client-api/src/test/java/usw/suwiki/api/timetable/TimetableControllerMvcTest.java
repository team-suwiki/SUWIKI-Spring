package usw.suwiki.api.timetable;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import usw.suwiki.comon.test.Table;
import usw.suwiki.comon.test.Tag;
import usw.suwiki.comon.test.support.AcceptanceTestSupport;
import usw.suwiki.comon.test.support.detail.ResponseValidator;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.lecture.timetable.TimetableRepository;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.comon.test.RequestType.POST;
import static usw.suwiki.comon.test.Table.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerMvcTest extends AcceptanceTestSupport {

  @Autowired
  private TimetableRepository timetableRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenAgent tokenAgent;

  private User user;
  private Claim claim;
  private String accessToken;

  @Override
  protected Set<Table> targetTables() {
    return Set.of(USERS, TIMETABLE, TIMETABLE_CELLS);
  }

  @Override
  @AfterEach
  protected void clean() {
    super.databaseCleaner.clean(targetTables());
  }

  @BeforeEach
  public void setup() {
    user = userRepository.save(User.init("loginId", "password", "test@suwiki.kr"));
    claim = new UserClaim(user.getLoginId(), user.getRole().name(), user.getRestricted());
    accessToken = tokenAgent.createAccessToken(user.getId(), claim);
  }

  @Nested
  @DisplayName("시간표 생성 테스트")
  class CreateTimetableTest {

    final String endpoint = "/timetables";

    @Test
    @DisplayName("시간표 생성 성공")
    void 시간표_생성_성공() throws Exception {
      // expected
      final String identifier = "create-timetable";
      final String summary = "[토큰 필요] 시간표 생성 API";
      final String description = "시간표를 생성하는 API 입니다. semester에는 [\"FIRST\", \"SECOND\", \"SUMMER\", \"WINTER\"] 만 입력 가능합니다.";
      final Tag tag = Tag.TIME_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.data.success", true));
      }};

      // setup
      var requestBody = new TimetableRequest.Description(2024, "first", "2024 1학기 시간표");

      // execution
      var result = perform(endpoint, POST, accessToken, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isCreated(), expectedResults);

      // database validation
      var timetables = timetableRepository.findAllByUserId(user.getId());
      assertAll(
          () -> assertThat(timetables.get(0)).isNotNull(),
          () -> assertThat(timetables.get(0).getName()).isEqualTo(requestBody.getName()),
          () -> assertThat(timetables.get(0).getSemester()).isEqualTo(requestBody.getSemester().toUpperCase()),
          () -> assertThat(timetables.get(0).getYear()).isEqualTo(requestBody.getYear())
      );

      // docs
      result.andDo(RestDocument.builder()
          .identifier(identifier)
          .summary(summary)
          .description(description)
          .tag(tag)
          .result(result)
          .generateDocs()
      );
    }

//    @ParameterizedTest
//    @CsvSource({
//        "2019, 1학기 시간표",
//        "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
//    })
//    @DisplayName("시간표 생성 실패 - 잘못된 연도, 긴 이름 입력")
//    void create_Fail_ByInvalidRequests(int year, String name) throws Exception {
//      // given
//      var requestBody = new TimetableRequest.Description(year, "first", name);
//
//      // when
//      var result = ApiRequestBuilder.builder()
//          .endpoint(endpoint)
//          .method(RequestType.POST)
//          .authorization(accessToken)
//          .requestBody(requestBody)
//          .build();
//
//      // then
//      then(result, status().isBadRequest(), expectExceptionJsonPath(result, PARAMETER_VALIDATION_FAIL));
//
//      // docs
//      result.andDo(
//          RestDocument.builder()
//              .identifier("create-timetable-fail-bad-request")
//              .tag(Tag.TIME_TABLE)
//              .result(result)
//              .generateDocs()
//      );
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"one", "third", "spring", "hello"})
//    @DisplayName("시간표 생성 실패 - 잘못된 학기 입력")
//    void create_Fail_ByWrongSemesterEntered(String semester) throws Exception {
//      // given
//      var request = new TimetableRequest.Description(2024, semester, "시간표입니다.");
//
//      // when
//      var result = mockMvc.perform(post(endpoint)
//          .header(AUTHORIZATION, accessToken)
//          .content(objectMapper.writeValueAsString(request))
//          .contentType(MediaType.APPLICATION_JSON)
//          .accept(MediaType.APPLICATION_JSON)
//      );
//
//      // then
//      result.andExpectAll(
//          status().isBadRequest(),
//          expectExceptionJsonPath(result, INVALID_TIMETABLE_SEMESTER)
//      );
//
//      // docs
//      result.andDo(
//          RestDocument.builder()
//              .identifier("create-timetable-fail-wrong-semester")
//              .tag(Tag.TIME_TABLE)
//              .result(result)
//              .generateDocs()
//      );
//    }
//
//    @Test
//    @DisplayName("시간표 생성 실패 - 잘못된 토큰")
//    void create_Fail_ByInvalidToken() throws Exception {
//      // given
//      var request = new TimetableRequest.Description(2024, "first", "2024 1학기 시간표");
//
//      // when
//      var result = mockMvc.perform(post(endpoint)
//          .header(AUTHORIZATION, INVALID_ACCESS_TOKEN)
//          .content(objectMapper.writeValueAsString(request))
//          .contentType(MediaType.APPLICATION_JSON)
//          .accept(MediaType.APPLICATION_JSON)
//      );
//
//      // then
//      result.andExpectAll(
//          status().isBadRequest(),
//          expectExceptionJsonPath(result, INVALID_TOKEN)
//      );
//
//      // docs
//      result.andDo(
//          RestDocument.builder()
//              .identifier("create-timetable-fail-invalid-token")
//              .tag(Tag.TIME_TABLE)
//              .result(result)
//              .generateDocs()
//      );
//    }
//  }
//
//    @Nested
//    @DisplayName("시간표 수정 테스트")
//    class UpdateTimetableTest {
//
//      private final String endpoint = "/timetables/{timetableId}";
//
//      private Timetable timetable;
//
//      @BeforeEach
//      void setup() {
//        timetable = timetableRepository.save(new Timetable(user.getId(), "이전 시간표", 2023, "first"));
//      }
//
//      @Test
//      @DisplayName("시간표 수정 성공")
//      void update_Success_WithoutException() throws Exception {
//        // given
//        var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");
//
//        // when
//        var result = mockMvc.perform(put(endpoint, timetable.getId())
//            .header(AUTHORIZATION, accessToken)
//            .content(objectMapper.writeValueAsString(request))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        result.andExpectAll(
//            status().isOk(),
//            jsonPath("$.data.success").value(true)
//        );
//
//        var timetables = timetableRepository.findAllByUserId(user.getId());
//        assertAll(
//            () -> assertThat(timetables.get(0)).isNotNull(),
//            () -> assertThat(timetables.get(0).getName()).isNotEqualTo(timetable.getName()),
//            () -> assertThat(timetables.get(0).getYear()).isNotEqualTo(timetable.getYear()),
//            () -> assertThat(timetables.get(0).getSemester()).isNotEqualTo(timetable.getSemester())
//        );
//
//        // docs
//        result.andDo(
//            RestDocument.builder()
//                .identifier("update-timetable-success")
//                .summary("[토큰 필요] 시간표 수정 API")
//                .description("시간표 수정 API 입니다. semester에는 [\"FIRST\", \"SECOND\", \"SUMMER\", \"WINTER\"] 만 입력 가능하니다.")
//                .tag(Tag.TIME_TABLE)
//                .result(result)
//                .generateDocs()
//        );
//      }
//
//      @Test
//      @DisplayName("시간표 수정 실패 - 작성자가 아님")
//      void update_Fail_ByNotAuthor() throws Exception {
//        // given
//        var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");
//
//        var anotherUserToken = tokenAgent.createAccessToken(2L, claim);
//
//        // when
//        var result = mockMvc.perform(put(endpoint, timetable.getId())
//            .header(AUTHORIZATION, anotherUserToken)
//            .content(objectMapper.writeValueAsString(request))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        result.andExpectAll(
//            status().is4xxClientError(),
//            expectExceptionJsonPath(result, NOT_AN_AUTHOR)
//        );
//
//        // docs
//        result.andDo(
//            RestDocument.builder()
//                .identifier("update-timetable-fail-not-author")
//                .tag(Tag.TIME_TABLE)
//                .result(result)
//                .generateDocs()
//        );
//      }
//
//      @ParameterizedTest
//      @CsvSource({
//          "2019, 1학기 시간표",
//          "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
//      })
//      @DisplayName("시간표 수정 실패 - 잘못된 연도, 긴 이름 입력")
//      void update_Fail_ByInvalidRequests(int year, String name) throws Exception {
//        // given
//        var request = new TimetableRequest.Description(year, "first", name);
//
//        // when
//        var result = mockMvc.perform(put(endpoint, timetable.getId())
//            .header(AUTHORIZATION, accessToken)
//            .content(objectMapper.writeValueAsString(request))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        result.andExpectAll(
//            status().isBadRequest(),
//            expectExceptionJsonPath(result, PARAMETER_VALIDATION_FAIL)
//        );
//
//        // docs
//        result.andDo(
//            RestDocument.builder()
//                .identifier("update-timetable-fail-bad-request")
//                .tag(Tag.TIME_TABLE)
//                .result(result)
//                .generateDocs()
//        );
//      }
//
//      @ParameterizedTest
//      @ValueSource(strings = {"one", "third", "spring", "hello"})
//      @DisplayName("시간표 수정 실패 - 잘못된 학기 입력")
//      void update_Fail_ByWrongSemesterEntered(String semester) throws Exception {
//        // given
//        var request = new TimetableRequest.Description(2024, semester, "시간표입니다.");
//
//        // when
//        var result = mockMvc.perform(put(endpoint, timetable.getId())
//            .header(AUTHORIZATION, accessToken)
//            .content(objectMapper.writeValueAsString(request))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        result.andExpectAll(
//            status().isBadRequest(),
//            expectExceptionJsonPath(result, INVALID_TIMETABLE_SEMESTER)
//        );
//
//        // docs
//        result.andDo(
//            RestDocument.builder()
//                .identifier("update-timetable-fail-wrong-semester")
//                .tag(Tag.TIME_TABLE)
//                .result(result)
//                .generateDocs()
//        );
//      }
//
//      @Test
//      @DisplayName("시간표 수정 실패 - 잘못된 토큰")
//      void update_Fail_ByInvalidToken() throws Exception {
//        // given
//        var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");
//
//        // when
//        var result = mockMvc.perform(put(endpoint, timetable.getId())
//            .header(AUTHORIZATION, INVALID_ACCESS_TOKEN)
//            .content(objectMapper.writeValueAsString(request))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON)
//        );
//
//        // then
//        result.andExpectAll(
//            status().isBadRequest(),
//            expectExceptionJsonPath(result, INVALID_TOKEN)
//        );
//
//        // docs
//        result.andDo(
//            RestDocument.builder()
//                .identifier("update-timetable-fail-invalid-token")
//                .tag(Tag.TIME_TABLE)
//                .result(result)
//                .generateDocs()
//        );
//      }
//    }

  }
}
