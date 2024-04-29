package usw.suwiki.api.timetable;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.common.test.Tag;
import usw.suwiki.common.test.annotation.AcceptanceTest;
import usw.suwiki.common.test.support.AcceptanceTestSupport;
import usw.suwiki.common.test.support.Uri;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.lecture.timetable.Timetable;
import usw.suwiki.domain.lecture.timetable.TimetableCell;
import usw.suwiki.domain.lecture.timetable.TimetableCellColor;
import usw.suwiki.domain.lecture.timetable.TimetableDay;
import usw.suwiki.domain.lecture.timetable.TimetableRepository;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.common.test.extension.AssertExtension.expectExceptionJsonPath;
import static usw.suwiki.core.exception.ExceptionType.INVALID_TIMETABLE_SEMESTER;
import static usw.suwiki.core.exception.ExceptionType.INVALID_TOKEN;
import static usw.suwiki.core.exception.ExceptionType.NOT_AN_AUTHOR;
import static usw.suwiki.core.exception.ExceptionType.OVERLAPPED_TIMETABLE_CELL_SCHEDULE;
import static usw.suwiki.core.exception.ExceptionType.PARAMETER_VALIDATION_FAIL;

@AcceptanceTest
class TimetableAcceptanceTest extends AcceptanceTestSupport {
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
    claim = new UserClaim("loginId", "USER", false);

    accessToken = tokenAgent.createAccessToken(user.getId(), claim);
  }

  @Nested
  class 시간표_생성_테스트 {
    private final String endpoint = "/timetables";

    @Test
    void 시간표_성생_성공() throws Exception {
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @CsvSource({
      "2019, 1학기 시간표",
      "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
    })
    void 시간표_생성_실패_잘못된_연도_긴_이름(int year, String name) throws Exception {
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"one", "third", "spring", "hello"})
    void 시간표_생성_실패_잘못된_학기(String semester) throws Exception {
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_생성_실패_잘못된_토큰() throws Exception {
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_수정_테스트 {
    private final String endpoint = "/timetables/{timetableId}";

    @Test
    void 시간표_수정_성공() throws Exception {
      // given
      var timetable = 시간표_생성();
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_수정_실패_다른_작성자() throws Exception {
      // given
      var timetable = 시간표_생성();
      var anotherUserToken = 다른_사용자_토큰_생성();

      var request = new TimetableRequest.Description(2024, "second", "수정된 시간표");

      // when
      var result = put(Uri.of(endpoint, timetable.getId()), anotherUserToken, request);

      // then
      result.andExpectAll(
        status().is4xxClientError(), // 403을 던지고 있음
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-fail-not-author")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @CsvSource({
      "2019, 1학기 시간표",
      "2024, 엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름엄청긴이름"
    })
    void 시간표_수정_실패_잘못된_연도_긴_이름(int year, String name) throws Exception {
      // given
      var timetable = 시간표_생성();
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"one", "third", "spring", "hello"})
    void 시간표_수정_실패_잘못된_학기(String semester) throws Exception {
      // given
      var timetable = 시간표_생성();
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_수정_실패_잘못된_토큰() throws Exception {
      // given
      var timetable = 시간표_생성();
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
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_삭제_테스트 {
    private final String endpoint = "/timetables/{timetableId}";

    @Test
    void 시간표_삭제_성공() throws Exception {
      // given
      var timetable = 시간표_생성();

      // when
      var result = delete(Uri.of(endpoint, timetable.getId()), accessToken);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.success").value(true)
      );

      assertThat(timetableRepository.findAll()).isEmpty();

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-timetable-success")
          .summary("[토큰 필요] 시간표 삭제 API")
          .description("시간표 삭제 API 입니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_삭제_실패_작성자가_아님() throws Exception {
      // given
      var timetable = 시간표_생성();
      var anotherUserToken = 다른_사용자_토큰_생성();

      // when
      var result = delete(Uri.of(endpoint, timetable.getId()), anotherUserToken);

      // then
      result.andExpectAll(
        status().is4xxClientError(),
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-timetable-fail-not-author")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_조회_테스트 {
    @Test
    void 시간표_모두_불러오기_성공() throws Exception {
      // given
      var timetable = 시간표_생성();
      시간표_생성2();

      // when
      var result = get(Uri.of("/timetables"), accessToken);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data[0].id").value(timetable.getId()),
        jsonPath("$.data[0].name").value(timetable.getName()),
        jsonPath("$.data[0].year").value(timetable.getYear()),
        jsonPath("$.data[0].semester").value(timetable.getSemester())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("get-all-timetables-success")
          .summary("[토큰 필요] 내 모든 시간표 조회 API")
          .description("내 시간표를 조회할 수 있는 API 입니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_단건_불러오기_성공() throws Exception {
      // given
      var timetable = 시간표_생성();
      var timetableCell = 시간표_셀_생성("MON", 2, 4);
      var timetableCell2 = 시간표_셀_생성("TUE", 1, 3);

      timetable.addCell(timetableCell);
      timetable.addCell(timetableCell2);
      timetableRepository.save(timetable);

      // when
      var result = get(Uri.of("/timetables/{timetableId}", timetable.getId()), accessToken);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.id").value(timetable.getId()),
        jsonPath("$.data.name").value(timetable.getName()),
        jsonPath("$.data.year").value(timetable.getYear()),
        jsonPath("$.data.semester").value(timetable.getSemester()),
        jsonPath("$.data.cells[0].cellIdx").value(timetable.getCells().indexOf(timetableCell)),
        jsonPath("$.data.cells[0].lecture").value(timetableCell.getLectureName()),
        jsonPath("$.data.cells[0].professor").value(timetableCell.getProfessorName()),
        jsonPath("$.data.cells[0].color").value(timetableCell.getColor()),
        jsonPath("$.data.cells[0].location").value(timetableCell.getLocation()),
        jsonPath("$.data.cells[0].day").value(timetableCell.getDay()),
        jsonPath("$.data.cells[0].startPeriod").value(timetableCell.getStartPeriod()),
        jsonPath("$.data.cells[0].endPeriod").value(timetableCell.getEndPeriod())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("get-timetable-success")
          .summary("[토큰 필요] 특정 시간표 조회 API")
          .description("특정 시간표의 모든 셀과 데이터를 가져오는 API 입니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_셀_추가_테스트 {
    private final String endpoint = "/timetables/{timetableId}/cells";

    @Transactional
    @ParameterizedTest
    @MethodSource("cellEnumInputs")
    void 시간표_셀_추가_성공(TimetableDay day, TimetableCellColor color) throws Exception {
      // given
      var timetable = 시간표_생성();

      var request = new TimetableRequest.Cell("강의", "교수님", color.name(), "강의실", day.name(), 1, 3);

      // when
      var result = post(Uri.of(endpoint, timetable.getId()), accessToken, request);

      // then
      result.andExpectAll(
        status().isCreated(),
        jsonPath("$.data.success").value(true)
      );

      var saved = timetableRepository.findById(timetable.getId()).orElseThrow();
      assertAll(
        () -> assertThat(saved.getCells()).isNotEmpty().hasSize(1),
        () -> assertThat(saved.getCells().get(0).getLectureName()).isEqualTo(request.getLecture()),
        () -> assertThat(saved.getCells().get(0).getProfessorName()).isEqualTo(request.getProfessor()),
        () -> assertThat(saved.getCells().get(0).getColor()).isEqualTo(request.getColor()),
        () -> assertThat(saved.getCells().get(0).getLocation()).isEqualTo(request.getLocation()),
        () -> assertThat(saved.getCells().get(0).getDay()).isEqualTo(request.getDay())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("insert-timetable-cell-success")
          .summary("[토큰 필요] 시간표 셀 추가 API")
          .description("""
            특정 시간표의 셀을 추가하는 API 입니다. 입력 가능한 값은 다음과 같습니다.
            day : 'MON','TUE','WED','THU','FRI','SAT','SUN','E_LEARNING'
            color : 'ORANGE','APRICOT','PINK','SKY','BROWN','LIGHT_BROWN','BROWN_DARK','PURPLE','PURPLE_LIGHT','RED_LIGHT','GREEN','GREEN_LIGHT','GREEN_DARK','NAVY','NAVY_LIGHT','NAVY_DARK','VIOLET','VIOLET_LIGHT','GRAY','GRAY_DARK'
            대소문자는 구분하지 않으니 참고바랍니다.
            """)
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    private static Stream<Arguments> cellEnumInputs() {
      return Arrays.stream(TimetableDay.values())
        .flatMap(day -> Arrays.stream(TimetableCellColor.values())
          .map(color -> Arguments.of(day, color)));
    }

    @Test
    void 시간표_셀_추가_실패_작성자_아님() throws Exception {
      // given
      var timetable = 시간표_생성();

      var request = new TimetableRequest.Cell("강의", "교수님", "ORANGE", "강의실", "MON", 1, 3);
      var anotherToken = 다른_사용자_토큰_생성();

      // when
      var result = post(Uri.of(endpoint, timetable.getId()), anotherToken, request);

      // then
      result.andExpectAll(
        status().is4xxClientError(),
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("insert-timetable-cell-fail-not-author")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_셀_추가_실패_중복된_셀() throws Exception {
      // given
      var timetable = 시간표_생성();
      timetable.addCell(시간표_셀_생성("MON", 1, 3));
      timetableRepository.save(timetable);

      var request = new TimetableRequest.Cell("강의", "교수님", "ORANGE", "강의실", "MON", 2, 4);

      // when
      var result = post(Uri.of(endpoint, timetable.getId()), accessToken, request);

      // then
      result.andExpectAll(
        status().isConflict(),
        expectExceptionJsonPath(result, OVERLAPPED_TIMETABLE_CELL_SCHEDULE)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("insert-timetable-cell-fail-overlapped-cell")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_셀_수정_테스트 {
    private final String endpoint = "/timetables/{timetableId}/cells/{cellIdx}";

    @Transactional
    @Test
    void 시간표_셀_수정_성공() throws Exception {
      // given
      var timetable = 시간표_생성();
      var timetableCell = 시간표_셀_생성("MON", 1, 3);
      var timetableCell2 = 시간표_셀_생성("TUE", 1, 3);

      timetable.addCell(timetableCell);
      timetable.addCell(timetableCell2);

      var cellIndex = timetable.getCells().size() - 1;

      timetableRepository.save(timetable);

      var request = new TimetableRequest.UpdateCell("강의2", "교수님2", "GRAY", "강의실2", "FRI", 3, 5);

      // when
      var result = put(Uri.of(endpoint, timetable.getId(), cellIndex), accessToken, request);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.success").value(true)
      );

      var saved = timetableRepository.findById(timetable.getId()).orElseThrow();
      assertAll(
        () -> assertThat(saved).isNotNull(),
        () -> assertThat(saved.getCells()).isNotEmpty().hasSize(2),
        () -> assertThat(saved.getCells().get(cellIndex).getLectureName()).isEqualTo(request.getLecture()),
        () -> assertThat(saved.getCells().get(cellIndex).getProfessorName()).isEqualTo(request.getProfessor()),
        () -> assertThat(saved.getCells().get(cellIndex).getColor()).isEqualTo(request.getColor()),
        () -> assertThat(saved.getCells().get(cellIndex).getLocation()).isEqualTo(request.getLocation()),
        () -> assertThat(saved.getCells().get(cellIndex).getDay()).isEqualTo(request.getDay()),
        () -> assertThat(saved.getCells().get(cellIndex).getStartPeriod()).isEqualTo(request.getStartPeriod()),
        () -> assertThat(saved.getCells().get(cellIndex).getEndPeriod()).isEqualTo(request.getEndPeriod())
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-cell-success")
          .summary("[토큰 필요] 시간표 셀 수정 API")
          .description("시간표 셀 수정 API 입니다. cell index 값은 내려준 값과 동일합니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_셀_수정_실패_작성자가_아님() throws Exception {
      // given
      var timetable = 시간표_생성();
      var anotherToken = 다른_사용자_토큰_생성();

      var request = new TimetableRequest.UpdateCell("강의2", "교수님2", "GRAY", "강의실2", "FRI", 3, 5);

      // when
      var result = put(Uri.of(endpoint, timetable.getId(), 0), anotherToken, request);

      // then
      result.andExpectAll(
        status().is4xxClientError(),
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-cell-fail-not-author")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_셀_수정_실패_중복된_셀() throws Exception {
      // given
      var timetable = 시간표_생성();
      timetable.addCell(시간표_셀_생성("MON", 1, 3));
      timetable.addCell(시간표_셀_생성("TUE", 1, 3));

      timetableRepository.save(timetable);

      var request = new TimetableRequest.UpdateCell("강의2", "교수님2", "GRAY", "강의실2", "MON", 2, 4);

      // when
      var result = put(Uri.of(endpoint, timetable.getId(), timetable.getCells().size() - 1), accessToken, request);

      // then
      result.andExpectAll(
        status().isConflict(),
        expectExceptionJsonPath(result, OVERLAPPED_TIMETABLE_CELL_SCHEDULE)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("update-timetable-cell-fail-overlapped-cell")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_셀_삭제_테스트 {
    private final String endpoint = "/timetables/{timetableId}/cells/{cellIdx}";

    @Test
    void 시간표_셀_삭제_성공() throws Exception {
      // given
      var timetable = 시간표_생성();
      var timetableCell = 시간표_셀_생성("mon", 1, 3);
      var timetableCell2 = 시간표_셀_생성("fri", 1, 3);

      timetable.addCell(timetableCell);
      timetable.addCell(timetableCell2);
      timetableRepository.save(timetable);

      var cellIndex = timetable.getCells().size() - 1;

      // when
      var result = delete(Uri.of(endpoint, timetable.getId(), cellIndex), accessToken);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.success").value(true)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-timetable-cell-success")
          .summary("[토큰 필요] 시간표 셀 삭제 API")
          .description("시간표 셀 삭제 API 입니다. cell index 값은 조회 API에서 내려준 값입니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }

    @Test
    void 시간표_셀_삭제_실패_작성자가_아님() throws Exception {
      // given
      var timetable = 시간표_생성();
      var anotherToken = 다른_사용자_토큰_생성();

      // when
      var result = delete(Uri.of(endpoint, timetable.getId(), 0), anotherToken);

      // then
      result.andExpectAll(
        status().is4xxClientError(),
        expectExceptionJsonPath(result, NOT_AN_AUTHOR)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("delete-timetable-cell-fail-not-author")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  @Nested
  class 시간표_동기화_테스트 {
    @Transactional
    @Test
    void 시간표_동기화_성공() throws Exception {
      // given
      var cells = List.of(
        new TimetableRequest.Cell("강의", "교수", "ORANGE", "강의실", "MON", 1, 3),
        new TimetableRequest.Cell("강의2", "교수2", "ORANGE", "강의실2", "TUE", 1, 3)
      );

      var request = List.of(
        new TimetableRequest.Bulk(new TimetableRequest.Description(2024, "first", "시간표"), cells),
        new TimetableRequest.Bulk(new TimetableRequest.Description(2024, "second", "시간표2"), cells)
      );

      // when
      var result = post(Uri.of("/timetables/bulk"), accessToken, request);

      // then
      result.andExpectAll(
        status().isOk(),
        jsonPath("$.data.success").value(true)
      );

      var timetables = timetableRepository.findAll();
      assertAll(
        () -> assertThat(timetables).isNotEmpty().hasSize(2),
        () -> assertThat(timetables.get(0).getCells()).isNotEmpty().hasSize(2),
        () -> assertThat(timetables.get(1).getCells()).isNotEmpty().hasSize(2)
      );

      // docs
      result.andDo(
        RestDocument.builder()
          .identifier("timetable-bulk-insert-success")
          .summary("[토큰 필요] 시간표 벌크 인서트 API")
          .description("시간표 벌크 인서트 API 입니다.")
          .tag(Tag.TIMETABLE)
          .result(result)
          .generateDocs()
      );
    }
  }

  private Timetable 시간표_생성() {
    return timetableRepository.save(new Timetable(user.getId(), "시간표", 2023, "first"));
  }

  private Timetable 시간표_생성2() {
    return timetableRepository.save(new Timetable(user.getId(), "시간표", 2024, "second"));
  }

  private TimetableCell 시간표_셀_생성(String day, int start, int end) {
    return new TimetableCell("강의", "교수님", "강의실", start, end, day, "ORANGE");
  }

  private String 다른_사용자_토큰_생성() {
    return tokenAgent.createAccessToken(0L, claim);
  }
}
