package usw.suwiki.domain.lecture.timetable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCell {
  @Column
  private String lectureName;

  @Column
  private String professorName;

  @Column
  private String location;

  @Column
  private Integer startPeriod;

  @Column
  private Integer endPeriod;

  @Enumerated(EnumType.STRING)
  private TimetableDay day;

  @Enumerated(EnumType.STRING)
  private TimetableCellColor color;

  public TimetableCell(String lecture, String professor, String location, Integer startPeriod, Integer endPeriod, String day, String color) {
    this.lectureName = lecture;
    this.professorName = professor;
    this.location = location;
    this.startPeriod = startPeriod;
    this.endPeriod = endPeriod;
    this.day = TimetableDay.from(day);
    this.color = TimetableCellColor.from(color);
    validatePeriod();
  }

  private void validatePeriod() {
    if (this.startPeriod > this.endPeriod) {
      throw new TimetableException(ExceptionType.INVALID_TIMETABLE_CELL_SCHEDULE);
    }
  }

  public boolean isOverlapped(TimetableCell cell) {
    return this.day.isEquals(cell.day) &&
           Math.max(this.startPeriod, cell.startPeriod) <= Math.min(this.endPeriod, cell.getEndPeriod());
  }

  public String getColor() {
    return this.color.name();
  }

  public String getDay() {
    return this.day.name();
  }
}
