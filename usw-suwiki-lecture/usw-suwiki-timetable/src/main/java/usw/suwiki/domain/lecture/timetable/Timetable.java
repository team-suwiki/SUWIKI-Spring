package usw.suwiki.domain.lecture.timetable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.TimetableException;
import usw.suwiki.infra.jpa.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "timetable_id"))
public class Timetable extends BaseEntity {
  @Column(nullable = false)
  private Long userId;

  @Column
  private String name;

  @Column
  private Integer year;

  @Enumerated(EnumType.STRING)
  private Semester semester;

  @ElementCollection
  @CollectionTable(name = "timetable_cells", joinColumns = @JoinColumn(name = "timetable_id"))
  @OrderColumn(name = "cell_idx")
  private final List<TimetableCell> cells = new ArrayList<>(); // todo: 반드시 테스트할 것

  public Timetable(Long userId, String name, Integer year, String semester) {
    this.userId = userId;
    this.name = name;
    this.year = year;
    this.semester = Semester.from(semester);
  }

  public void update(String name, Integer year, String semester) {
    this.name = name;
    this.year = year;
    this.semester = Semester.from(semester);
  }

  public String getSemester() {
    return this.semester.name();
  }

  public void validateAuthor(Long userId) {
    if (!this.userId.equals(userId)) {
      throw new TimetableException(ExceptionType.TIMETABLE_NOT_AN_AUTHOR);
    }
  }

  public void addCell(TimetableCell cell) {
    validateOverlap(cell);
    this.cells.add(cell);
  }

  public void updateCell(int cellIdx, TimetableCell cell) {
    this.cells.remove(cellIdx);
    addCell(cell);
  }

  public void removeCell(int cellIdx) {
    this.cells.remove(cellIdx);
  }

  private void validateOverlap(TimetableCell cell) {
    if (cells.stream().anyMatch(cell::isOverlapped)) {
      throw new TimetableException(ExceptionType.OVERLAPPED_TIMETABLE_CELL_SCHEDULE);
    }
  }
}
