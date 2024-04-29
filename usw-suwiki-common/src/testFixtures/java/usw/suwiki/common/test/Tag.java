package usw.suwiki.common.test;

import io.github.hejow.restdocs.document.ApiTag;

public enum Tag implements ApiTag {
  TIMETABLE("시간표 API"),
  LECTURE("강의 API"),
  USER("유저 API"),
  ;

  private final String description;

  Tag(String description) {
    this.description = description;
  }

  @Override
  public String getName() {
    return description;
  }
}
