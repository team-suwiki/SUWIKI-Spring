package usw.suwiki.domain.exampost.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExamPostRequest {

  @Data
  public static class Create {
    @NotBlank
    private final String lectureName;
    @NotBlank
    private final String selectedSemester;
    @NotBlank
    private final String professor;
    @NotBlank
    private final String examType;
    @NotBlank
    private final String examInfo;
    @NotBlank
    private final String examDifficulty;
    @NotBlank
    private final String content;
  }

  @Data
  public static class Update {
    @NotBlank
    private final String selectedSemester;
    @NotBlank
    private final String examType;
    @NotBlank
    private final String examInfo;
    @NotBlank
    private final String examDifficulty;
    @NotBlank
    private final String content;
  }
}
