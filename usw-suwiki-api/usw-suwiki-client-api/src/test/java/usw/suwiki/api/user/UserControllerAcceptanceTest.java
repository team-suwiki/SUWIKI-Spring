package usw.suwiki.api.user;

import io.github.hejow.restdocs.document.RestDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ActiveProfiles;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.auth.token.ConfirmationTokenRepository;
import usw.suwiki.auth.token.response.ConfirmResponse;
import usw.suwiki.common.test.Table;
import usw.suwiki.common.test.support.AcceptanceTestSupport;
import usw.suwiki.common.test.support.ResponseValidator;
import usw.suwiki.common.test.support.Uri;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.core.secure.model.Claim;
import usw.suwiki.domain.user.User;
import usw.suwiki.domain.user.UserRepository;
import usw.suwiki.domain.user.dto.UserRequestDto.*;
import usw.suwiki.domain.user.model.UserClaim;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static usw.suwiki.common.test.Table.USERS;
import static usw.suwiki.common.test.Tag.USER_TABLE;

@ActiveProfiles("mysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerAcceptanceTest extends AcceptanceTestSupport {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;

  @Autowired
  private TokenAgent tokenAgent;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private User user;
  private ConfirmationToken confirmationToken;
  private Claim claim;
  private String accessToken;
  private final String loginId = "suwiki";
  private final String password = "p@sSw0rc1!!";
  private final String email = "suwiki@suwon.ac.kr";

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
    user = userRepository.save(User.init(loginId, passwordEncoder.encode(password), email));
    confirmationToken = confirmationTokenRepository.save(new ConfirmationToken(user.getId()));
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
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", true));
      }};

      // setup
      var requestBody = new CheckLoginIdForm(loginId);

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

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
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", false));
      }};

      // setup
      var requestBody = new CheckLoginIdForm("diger");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

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
      var requestBody = new CheckLoginIdForm("");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

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
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", true));
      }};

      // setup
      var requestBody = new CheckEmailForm(email);

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

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
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.overlap", false));
      }};

      // setup
      var requestBody = new CheckEmailForm("diger@suwon.ac.kr");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

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
      var requestBody = new CheckEmailForm("");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.PARAMETER_VALIDATION_FAIL);
    }
  }

  @Nested
  @DisplayName("유저 회원가입 테스트")
  class JoinTest {

    final String endpoint = "/user/join";

    @Test
    void 회원가입_성공() throws Exception {
      // expected
      final String identifier = "join";
      final String summary = "회원가입 API";
      final String description = "회원가입 API입니다. Body에는 String 타입의 \"LoginId\", \"Password\", \"Email\"을 입력해야하며 모든 필드가 Blank 제약조건이 있습니다.";
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.success", true));
      }};

      // setup
      var requestBody = new JoinForm("diger", "digerpassword1!", "diger@suwon.ac.kr");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

      // db validation
      Optional<User> diger = userRepository.findByLoginId("diger");

      assertAll(
          () -> assertThat(diger.get()).isNotNull(),
          () -> assertThat(diger.get().getEmail()).isEqualTo(requestBody.email()),
          () -> assertTrue(passwordEncoder.matches(requestBody.password(), diger.get().getPassword()))
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

    @Test
    void 회원가입_실패_아이디_중복() throws Exception {
      // setup
      var requestBody = new JoinForm(loginId, "digerPassword123!", "diger@gmail.com");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.LOGIN_ID_OR_EMAIL_OVERLAP);
    }

    @Test
    void 회원가입_실패_이메일_중복() throws Exception {
      // setup
      var requestBody = new JoinForm("diger", "digerPassword123!", email);

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.LOGIN_ID_OR_EMAIL_OVERLAP);
    }

    @Test
    void 회원가입_실패_값_누락() throws Exception {
      // setup
      var requestBody = new JoinForm("diger", "", "test@suwiki.kr");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.PARAMETER_VALIDATION_FAIL);
    }

    @Test
    void 회원가입_실패_교내이메일이아닌경우() throws Exception {
      // setup
      var requestBody = new JoinForm("diger", "digerPassword123!", "diger@gmail.com");

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isBadRequest(), ExceptionType.IS_NOT_EMAIL_FORM);
    }
  }

  @Nested
  @DisplayName("유저 이메일 인증 테스트")
  class VerifyEmailTest {

    final String endpoint = "/user/verify-email";

    @Test
    void 이메일_인증_성공() throws Exception {
      // expected
      final String identifier = "verify-email";
      final String summary = "이메일 인증 API";
      final String description = "이메일 인증 API입니다. Parameter에는 \"token\"을 Key로 갖고 값을 입력해야합니다.";
      final var tag = USER_TABLE;
      final String expectedResults = ConfirmResponse.SUCCESS.getContent();

      // setup
      final String emailVerificationToken = confirmationToken.getToken();
      final List<Pair<String, String>> parameter = new ArrayList<>();
      parameter.add(Pair.of("token", emailVerificationToken));

      // execution
      var result = getNonJson(Uri.of(endpoint), null, parameter);

      // result validation
      ResponseValidator.validateNonJSONResponse(result, status().isOk(), expectedResults);

      // db validation
      Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByToken(emailVerificationToken);

      assertAll(
          () -> assertThat(confirmationToken.get()).isNotNull(),
          () -> assertTrue(confirmationToken.get().isVerified())
      );

      // Non DOCS
    }

    @Test
    void 이메일_인증_실패_잘못된_토큰() throws Exception {
      // setup
      final String emailVerificationToken = confirmationToken.getToken();
      final List<Pair<String, String>> parameter = new ArrayList<>();
      parameter.add(Pair.of("token", emailVerificationToken + "diger"));

      // execution
      var result = getNonJson(Uri.of(endpoint), null, parameter);

      // result validation
      ResponseValidator.validate(result, status().isOk(), ExceptionType.valueOf(ConfirmResponse.ERROR.getContent()));
    }

    @Test
    void 이메일_인증_실패_만료된_토큰() throws Exception {
      // setup
      // Reflection For Modify ExpiresAt
      Constructor<ConfirmationToken> constructor = ConfirmationToken.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      ConfirmationToken reflectedConfirmationToken = constructor.newInstance();

      setFieldValue(reflectedConfirmationToken, "id", 2L);
      setFieldValue(reflectedConfirmationToken, "userIdx", 1L);
      setFieldValue(reflectedConfirmationToken, "token", "tokenValue");
      setFieldValue(reflectedConfirmationToken, "createdAt", LocalDateTime.now());
      setFieldValue(reflectedConfirmationToken, "expiresAt", LocalDateTime.now().minusMinutes(200));
      setFieldValue(reflectedConfirmationToken, "confirmedAt", null);
      confirmationTokenRepository.save(reflectedConfirmationToken);

      final String emailVerificationToken = confirmationToken.getToken();
      final List<Pair<String, String>> parameter = new ArrayList<>();
      parameter.add(Pair.of("token", emailVerificationToken));

      // execution
      var result = getNonJson(Uri.of(endpoint), null, parameter);

      // result validation
      ResponseValidator.validate(result, status().isOk(), ExceptionType.valueOf(ConfirmResponse.EXPIRED.getContent()));

      // db validation
      Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByToken(emailVerificationToken);
      assertAll(() -> assertThat(confirmationToken.get()).isNull());
    }
  }

  @Nested
  @DisplayName("아이디 찾기 테스트")
  class FindIdTest {

    final String endpoint = "/user/find-id";

    @Test
    void 아이디_찾기_성공() throws Exception {
      // expected
      final String identifier = "find-id";
      final String summary = "아이디 찾기 API";
      final String description = "아이디 찾기 API입니다.";
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.success", true));
      }};

      // setup
      var requestBody = new FindIdForm(email);

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

      // non-db validation

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
  }

  @Nested
  @DisplayName("비밀번호 찾기 테스트")
  class FindPwTest {

    final String endpoint = "/user/find-pw";

    @Test
    void 비밀번호_찾기_성공() throws Exception {
      // expected
      final String identifier = "find-id";
      final String summary = "비밀번호 찾기 API";
      final String description = "비밀번호 찾기 API입니다.";
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.success", true));
      }};

      // setup
      var requestBody = new FindPasswordForm(loginId, email);

      // execution
      var result = post(Uri.of(endpoint), null, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

      // non-db validation

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
  }

  @Nested
  @DisplayName("비밀번호 초기화 테스트")
  class RestPwTest {

    final String endpoint = "/user/reset-pw";

    @Test
    void 비밀번호_초기화_성공() throws Exception {
      // expected
      final String identifier = "reset-id";
      final String summary = "비밀번호 초기화 API";
      final String description = "비밀번호 초기화 API입니다.";
      final var tag = USER_TABLE;
      final List<Pair<String, Object>> expectedResults = new ArrayList<>() {{
        add(Pair.of("$.success", true));
      }};

      // setup
      String newPassword = "newPassword1!";
      var requestBody = new EditMyPasswordForm(password, newPassword);

      // execution
      var result = post(Uri.of(endpoint), accessToken, requestBody);

      // result validation
      ResponseValidator.validate(result, status().isOk(), expectedResults);

      // db validation
      Optional<User> diger = userRepository.findByLoginId(loginId);

      assertAll(
          () -> assertThat(diger.get()).isNotNull(),
          () -> assertTrue(passwordEncoder.matches(newPassword, diger.get().getPassword()))
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
  }

  private static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    Field field = object.getClass().getDeclaredField(fieldName);
    field.setAccessible(true); // private 필드에 접근 가능하도록 설정
    field.set(object, value);
  }
}
