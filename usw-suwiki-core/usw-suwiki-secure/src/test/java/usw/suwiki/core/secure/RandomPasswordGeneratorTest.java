package usw.suwiki.core.secure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RandomPasswordGeneratorTest {

  @RepeatedTest(10)
  @DisplayName("랜덤한 비밀번호를 생성하면 8자리 이하여야 한다.")
  void generate_Success_Within8Letters() {
    // given

    // when
    var password = RandomPasswordGenerator.generate();

    // then
    assertAll(
      () -> assertThat(password).isNotBlank(),
      () -> assertThat(password).hasSizeLessThanOrEqualTo(8)
    );
  }
}
