package usw.suwiki.domain.evaluatepost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvaluatePostRequest {

  @Data
  public static class Create {
    @NotBlank
    private final String content;
    @NotBlank
    private final String lectureName;
    @NotBlank
    private final String selectedSemester;
    @NotBlank
    private final String professor;
    @PositiveOrZero
    private final float satisfaction;
    @PositiveOrZero
    private final float learning;
    @PositiveOrZero
    private final float honey;
    @PositiveOrZero
    private final int team;
    @PositiveOrZero
    private final int difficulty;
    @PositiveOrZero
    private final int homework;
  }

  @Data
  public static class Update {
    @NotBlank
    private final String content;
    @NotBlank
    private final String selectedSemester;
    @PositiveOrZero
    private final float satisfaction;
    @PositiveOrZero
    private final float learning;
    @PositiveOrZero
    private final float honey;
    @PositiveOrZero
    private final int team;
    @PositiveOrZero
    private final int difficulty;
    @PositiveOrZero
    private final int homework;
  }
}
