package usw.suwiki.core.secure.model;

public interface Claim {
  String loginId();

  String role();

  boolean restricted();
}
