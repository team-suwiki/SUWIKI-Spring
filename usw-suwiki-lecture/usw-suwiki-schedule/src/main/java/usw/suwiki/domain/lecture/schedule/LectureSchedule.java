package usw.suwiki.domain.lecture.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureSchedule extends BaseEntity {
  @Column(nullable = false)
  private Long lectureId;

  @Column(name = "place_schedule", nullable = false)
  private String placeSchedule;  // 장소와 시간

  @Column(nullable = false, updatable = false)
  private String semester;
}
