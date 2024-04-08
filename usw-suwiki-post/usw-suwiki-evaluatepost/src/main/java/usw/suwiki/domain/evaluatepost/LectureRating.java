package usw.suwiki.domain.evaluatepost;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureRating {
  private float satisfaction;
  private float learning;
  private float honey;
  private float totalAvg;
  private int team;
  private int difficulty;
  private int homework;

  public LectureRating(float satisfaction, float learning, float honey, int team, int difficulty, int homework) {
    this.satisfaction = satisfaction;
    this.learning = learning;
    this.honey = honey;
    this.totalAvg = (learning + honey + satisfaction) / 3;
    this.team = team;
    this.difficulty = difficulty;
    this.homework = homework;
  }
}
