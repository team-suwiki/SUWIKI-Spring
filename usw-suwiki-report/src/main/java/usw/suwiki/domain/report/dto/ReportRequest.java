package usw.suwiki.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportRequest {

  @Data
  public static class Evaluate {
    @NotNull
    private final Long evaluateIdx;
  }

  @Data
  public static class Exam {
    @NotNull
    private final Long examIdx;
  }
}
