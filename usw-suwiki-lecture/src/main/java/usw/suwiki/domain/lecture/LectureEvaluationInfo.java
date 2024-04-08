package usw.suwiki.domain.lecture;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.domain.lecture.model.Evaluation;

@Getter
@Embeddable
@NoArgsConstructor
public class LectureEvaluationInfo {
  private static final int EVALUATION_TYPE_COUNT = 3;
  private static final int ZERO_POST = 0;
  private static final int MINUS = -1;
  private static final int PLUS = 1;

  private float lectureTotalAvg;
  private float lectureSatisfactionAvg;
  private float lectureHoneyAvg;
  private float lectureLearningAvg;
  private float lectureTeamAvg;
  private float lectureDifficultyAvg;
  private float lectureHomeworkAvg;

  private float lectureSatisfactionValue;
  private float lectureHoneyValue;
  private float lectureLearningValue;
  private float lectureTeamValue;
  private float lectureDifficultyValue;
  private float lectureHomeworkValue;

  public void calculateAverage(int postsCount) {
    this.lectureSatisfactionAvg = average(this.lectureSatisfactionValue, postsCount);
    this.lectureHoneyAvg = average(this.lectureHoneyValue, postsCount);
    this.lectureLearningAvg = average(this.lectureLearningValue, postsCount);
    this.lectureTeamAvg = average(this.lectureTeamValue, postsCount);
    this.lectureDifficultyAvg = average(this.lectureDifficultyValue, postsCount);
    this.lectureHomeworkAvg = average(this.lectureHomeworkValue, postsCount);
    this.lectureTotalAvg = (lectureSatisfactionAvg + lectureHoneyAvg + lectureLearningAvg) / EVALUATION_TYPE_COUNT;
  }

  public void apply(Evaluation data) {
    calculate(data, PLUS);
  }

  public void cancel(Evaluation data) {
    calculate(data, MINUS);
  }

  private void calculate(Evaluation data, int operator) {
    this.lectureSatisfactionValue += operator * data.satisfaction();
    this.lectureHoneyValue += operator * data.honey();
    this.lectureLearningValue += operator * data.learning();
    this.lectureTeamValue += operator * data.team();
    this.lectureDifficultyValue += operator * data.difficulty();
    this.lectureHomeworkValue += operator * data.homework();
  }

  private float average(float amount, int count) {
    return count == ZERO_POST ? 0.0f : (amount / count);
  }
}
