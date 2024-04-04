package usw.suwiki.domain.lecture.timetable.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableRequest {

  @Data
  public static class Bulk {
    @Valid
    private final Description description;
    @Valid
    private final List<Cell> cells;
  }

  @Data
  public static class Description {
    @NotNull
    @Min(2020)
    private final Integer year;
    @NotNull
    @Size(max = 10)
    private final String semester;
    @NotNull
    @Size(max = 30)
    private final String name;
  }

  @Data
  public static class Cell {
    @NotNull
    @Size(max = 150)
    private final String lecture;
    @NotNull
    @Size(max = 130)
    private final String professor;
    @NotNull
    @Size(max = 50)
    private final String color;
    @NotNull
    @Size(max = 150)
    private final String location;
    @NotNull
    @Size(max = 50)
    private final String day;
    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;
    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;
  }

  @Data
  public static class UpdateCell {
    @PositiveOrZero
    private final int cellIdx;
    @NotNull
    @Size(max = 150)
    private final String lecture;
    @NotNull
    @Size(max = 130)
    private final String professor;
    @NotNull
    @Size(max = 50)
    private final String color;
    @NotNull
    @Size(max = 150)
    private final String location;
    @NotNull
    @Size(max = 50)
    private final String day;
    @Min(value = 1)
    @Max(value = 24)
    private Integer startPeriod;
    @Min(value = 1)
    @Max(value = 24)
    private Integer endPeriod;
  }
}
