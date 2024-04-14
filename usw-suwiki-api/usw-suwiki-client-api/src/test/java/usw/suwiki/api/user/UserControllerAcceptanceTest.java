package usw.suwiki.api.user;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import usw.suwiki.comon.test.Table;
import usw.suwiki.comon.test.Tag;
import usw.suwiki.comon.test.support.AcceptanceTestSupport;
import usw.suwiki.comon.test.support.detail.ResponseValidator;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.dto.UserRequestDto;
import usw.suwiki.domain.user.model.UserClaim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.comon.test.RequestType.POST;
import static usw.suwiki.comon.test.Table.USERS;

@ActiveProfiles("mysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerAcceptanceTest extends AcceptanceTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TokenAgent tokenAgent;

  private User user;
  private Claim claim;
  private String accessToken;

  @Override
  protected Set<Table> targetTables() {
    return Set.of(USERS);
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
  @DisplayName("유저 아이디 중복 확인 테스트")
  class CheckIdTest {

    final String endpoint = "/user/check-id";

    @Test
    void 아이디_중복확인_성공_중복일_시() throws Exception {
      // expected
      final String identifier = "check-id";
      final String summary = "아이디 중복 확인 (중복일 시) API";
      final String description = "아이디 중복 확인 (중복일 시) API입니다. Body에는 String 타입을 입력해야하며 Blank 제약조건이 있습니다.";
      final Tag tag = Tag.USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", true));
      }};

      // setup
      var requestBody = new UserRequestDto.CheckLoginIdForm("loginId");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

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

    @Test
    void 아이디_중복확인_성공_중복_아닐_시() throws Exception {
      // expected
      final String identifier = "check-id";
      final String summary = "아이디 중복 확인 (중복 아닐 시) API";
      final String description = "아이디 중복 확인 (중복 아닐 시) API입니다. Body에는 String 타입을 입력해야하며 Blank 제약조건이 있습니다.";
      final Tag tag = Tag.USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", false));
      }};

      // setup
      var requestBody = new UserRequestDto.CheckLoginIdForm("diger");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

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

    @Test
    void 아이디_중복확인_실패_잘못된_요청값() throws Exception {
      // setup
      var requestBody = new UserRequestDto.CheckLoginIdForm("");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.PARAMETER_VALIDATION_FAIL);
    }
  }

  @Nested
  @DisplayName("유저 이메일 중복 확인 테스트")
  class CheckEmailTest {

    final String endpoint = "/user/check-email";

    @Test
    void 아이디_중복확인_성공_중복일_시() throws Exception {
      // expected
      final String identifier = "check-email";
      final String summary = "이메일 중복 확인 (중복일 시) API";
      final String description = "이메일 중복 확인 (중복일 시) API입니다. Body에는 String 타입을 입력해야하며 Blank 제약조건이 있습니다.";
      final Tag tag = Tag.USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", true));
      }};

      // setup
      var requestBody = new UserRequestDto.CheckEmailForm("test@suwiki.kr");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

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

    @Test
    void 아이디_중복확인_성공_중복_아닐_시() throws Exception {
      // expected
      final String identifier = "check-email";
      final String summary = "이메일 중복 확인 (중복일 시) API";
      final String description = "이메일 중복 확인 (중복일 시) API입니다. Body에는 String 타입을 입력해야하며 Blank 제약조건이 있습니다.";
      final Tag tag = Tag.USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", false));
      }};

      // setup
      var requestBody = new UserRequestDto.CheckEmailForm("diger@suwiki.kr");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

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

    @Test
    void 아이디_중복확인_실패_잘못된_요청값() throws Exception {
      // setup
      var requestBody = new UserRequestDto.CheckEmailForm("");

      // execution
      var result = perform(endpoint, POST, null, null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.PARAMETER_VALIDATION_FAIL);
    }
  }
}
