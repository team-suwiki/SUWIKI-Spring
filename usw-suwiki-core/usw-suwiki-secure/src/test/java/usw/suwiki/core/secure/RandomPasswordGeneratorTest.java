package usw.suwiki.core.secure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RandomPasswordGeneratorTest {
  private static final String[] SYMBOLS = {"!", "@", "#", "$", "%", "^"};

  @RepeatedTest(10)
  @DisplayName("랜덤한 비밀번호를 생성하면 8자리 이하의 특수문자를 포함한 값이어야 한다.")
  void generate_Success_Within8Letters() {
    // given

    // when
    var password = RandomPasswordGenerator.generate();

    // then
    assertAll(
      () -> assertThat(password).isNotBlank(),
      () -> assertThat(password).hasSize(8),
      () -> assertThat(password).containsAnyOf(SYMBOLS)
    );
  }
}
