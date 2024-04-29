package usw.suwiki.common.test.support;

public final class Pair {
  private final String first;
  private final String second;

  private Pair(String first, String second) {
    this.first = first;
    this.second = second;
  }

  public static Pair parameter(String first, String second) {
    return new Pair(first, second);
  }

  public static Pair parameter(String first, int second) {
    return new Pair(first, String.valueOf(second));
  }

  public static Pair parameter(String first, long second) {
    return new Pair(first, String.valueOf(second));
  }

  public String first() {
    return first;
  }

  public String second() {
    return second;
  }
}
