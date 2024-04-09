package usw.suwiki.api.timetable;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import usw.suwiki.comon.test.Tag;
import usw.suwiki.comon.test.support.WebMvcTestSupport;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.lecture.timetable.TimetableRepository;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.model.UserClaim;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.core.exception.ExceptionType.INVALID_TIMETABLE_SEMESTER;
import static usw.suwiki.core.exception.ExceptionType.PARAMETER_VALIDATION_FAIL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimetableControllerTest extends WebMvcTestSupport {
  @Autowired
  private TimetableRepository timetableRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenAgent tokenAgent;

  private User user;
  private String accessToken;

  @BeforeEach
  void setup() {
    user = userRepository.save(User.init("loginId", "password", "test@suwiki.kr"));
    var claim = new UserClaim(user.getLoginId(), user.getRole().name(), user.getRestricted());

    accessToken = tokenAgent.createAccessToken(user.getId(), claim);
  }

  @Nested
  @DisplayName("시간표 생성 테스트")
  class CreateTimetableTest {
    private final String endpoint = "/timetables";

    @Test
    @DisplayName("시간표를 생성한 뒤 유저 아이디로 만든 시간표에 접근할 수 있어야 한다.")
    void create_Success_WithoutException() throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, "first", "2024 1학기 시간표");

      // when
      var result = mockMvc.perform(post(endpoint)
        .header(AUTHORIZATION, accessToken)
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
      );

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
    @DisplayName("시간표를 생성할 때 잘못된 연도나 너무 긴 이름을 넣으면 bean validation 예외가 발생한다.")
    void create_Fail_ByInvalidRequests(int year, String name) throws Exception {
      // given
      var request = new TimetableRequest.Description(year, "first", name);

      // when
      var result = mockMvc.perform(post(endpoint)
        .header(AUTHORIZATION, accessToken)
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
      );

      // then
      result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.code").value(PARAMETER_VALIDATION_FAIL.getCode()),
        jsonPath("$.message").value(PARAMETER_VALIDATION_FAIL.getMessage()),
        jsonPath("$.status").value(PARAMETER_VALIDATION_FAIL.getStatus())
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
    @DisplayName("시간표를 생성할 때 잘못된 학기가 들어오면 생성에 실패한다.")
    void create_Fail_ByWrongSemesterEntered(String semester) throws Exception {
      // given
      var request = new TimetableRequest.Description(2024, semester, "시간표입니다.");

      // when
      var result = mockMvc.perform(post(endpoint)
        .header(AUTHORIZATION, accessToken)
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
      );

      // then
      result.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.code").value(INVALID_TIMETABLE_SEMESTER.getCode()),
        jsonPath("$.message").value(INVALID_TIMETABLE_SEMESTER.getMessage()),
        jsonPath("$.status").value(INVALID_TIMETABLE_SEMESTER.getStatus())
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
  }
}
