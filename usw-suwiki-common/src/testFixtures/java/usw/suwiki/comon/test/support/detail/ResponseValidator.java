package usw.suwiki.comon.test.support.detail;

import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import usw.suwiki.core.exception.ExceptionType;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static usw.suwiki.comon.test.extension.AssertExtension.expectExceptionJsonPath;

public final class ResponseValidator {

  public static void validate(ResultActions resultActions, ResultMatcher expectedStatus, List<Pair<String, Object>> expectedResults) throws Exception {
    for (Pair<String, Object> expectedResult : expectedResults) {
      resultActions.andExpectAll(
          expectedStatus,
          jsonPath(expectedResult.getFirst()).value(expectedResult.getSecond())
      );
    }
  }

  public static void validate(ResultActions resultActions, ResultMatcher expectedStatus, ExceptionType expectedException) throws Exception {
    resultActions.andExpectAll(
        expectedStatus,
        expectExceptionJsonPath(resultActions, expectedException)
    );
  }
}
