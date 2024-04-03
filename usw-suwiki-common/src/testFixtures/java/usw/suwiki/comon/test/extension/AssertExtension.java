package usw.suwiki.comon.test.extension;

import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.assertj.core.api.ThrowableTypeAssert;
import usw.suwiki.core.exception.BaseException;
import usw.suwiki.core.exception.ExceptionType;

public final class AssertExtension {

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
