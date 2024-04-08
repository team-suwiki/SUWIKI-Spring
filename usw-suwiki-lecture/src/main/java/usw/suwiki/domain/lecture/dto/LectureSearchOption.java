package usw.suwiki.domain.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LectureSearchOption { // todo: apply validation on controller
  @NotBlank
  private final String orderOption;

  @PositiveOrZero
  private final Integer pageNumber;

  @NotBlank
  private final String majorType;

  public boolean passMajorFiltering() {
    return majorType.equals("전체");
  }
}
