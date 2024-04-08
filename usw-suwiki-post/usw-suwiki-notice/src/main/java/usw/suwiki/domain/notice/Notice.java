package usw.suwiki.domain.notice;

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
public class Notice extends BaseEntity {
  @Column(columnDefinition = "text")
  private String title;

  @Column(columnDefinition = "text")
  private String content;

  public void update(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
