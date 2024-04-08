package usw.suwiki.domain.viewexam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseEntity;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewExam extends BaseEntity {
  @Column(nullable = false)
  private Long userIdx;

  @Column(nullable = false)
  private Long lectureId;
}
