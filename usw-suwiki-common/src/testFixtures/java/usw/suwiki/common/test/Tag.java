package usw.suwiki.common.test;

import io.github.hejow.restdocs.document.ApiTag;

public enum Tag implements ApiTag {
  TIME_TABLE("시간표 API"),
  USER_TABLE("유저 API"),
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
