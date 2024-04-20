package usw.suwiki.common.test.extension;

import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.assertj.core.api.ThrowableTypeAssert;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import usw.suwiki.core.exception.BaseException;
import usw.suwiki.core.exception.ExceptionType;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class AssertExtension {

  public static ResultMatcher expectExceptionJsonPath(ResultActions resultActions, ExceptionType exceptionType) {
    return result -> Objects.requireNonNull(resultActions).andExpectAll(
      jsonPath("$.code").value(exceptionType.getCode()),
      jsonPath("$.message").value(exceptionType.getMessage()),
      jsonPath("$.status").value(exceptionType.getStatus())
    );
  }

  public static CustomThrowableTypeAssert assertThatApplicationException(ExceptionType exceptionType) {
    return new CustomThrowableTypeAssert(BaseException.class, exceptionType);
  }

  public static class CustomThrowableTypeAssert extends ThrowableTypeAssert<BaseException> {
    private final ExceptionType exceptionType;

    public CustomThrowableTypeAssert(Class<? extends BaseException> throwableType, ExceptionType exceptionType) {
      super(throwableType);
      this.exceptionType = exceptionType;
    }

    @Override
    public ThrowableAssertAlternative<BaseException> isThrownBy(ThrowableAssert.ThrowingCallable throwingCallable) {
      return super.isThrownBy(throwingCallable)
        .withMessage(exceptionType.getMessage());
    }
  }
}
