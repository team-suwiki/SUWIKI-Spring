package usw.suwiki.statistics.log;

import static java.lang.Character.toUpperCase;

public enum MonitorOption {
  LECTURE,
  EVALUATE_POSTS,
  EXAM_POSTS,
  USER,
  NOTICE,
  ADMIN,
  ;

  public String toCamelCase() {
    var split = name().split("_");
    var first = split[0].toLowerCase();
    return split.length == 1 ? first : first + toUpperCase(split[1].charAt(0)) + split[1].substring(1).toLowerCase();
  }
}
