package usw.suwiki.common.test.support;

import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import usw.suwiki.common.test.extension.AssertExtension;
import usw.suwiki.core.exception.ExceptionType;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class ResponseValidator {

  public static void validate(
      ResultActions result,
      ResultMatcher expectedStatus,
      List<Pair<String, Object>> expects
  ) throws Exception {
    for (Pair<String, Object> expect : expects) {
      result.andExpectAll(
          expectedStatus,
          jsonPath(expect.getFirst()).value(expect.getSecond())
      );
    }
  }

  public static void validate(
      ResultActions result,
      ResultMatcher expectedStatus,
      ExceptionType expectedException
  ) throws Exception {
    result.andExpectAll(
        expectedStatus,
        AssertExtension.expectExceptionJsonPath(result, expectedException)
    );
  }

  public static void validateNonJSONResponse(
      ResultActions result,
      ResultMatcher expectedStatus,
      Object expectedResult
  ) throws Exception {
    result.andExpectAll(
        expectedStatus,
        content().string(expectedResult.toString())
    );
  }
}
