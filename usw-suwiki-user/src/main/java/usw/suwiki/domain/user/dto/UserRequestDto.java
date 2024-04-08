package usw.suwiki.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public final class UserRequestDto {

  public record CheckLoginIdForm(@NotBlank String loginId) {
  }

  public record CheckEmailForm(@NotBlank String email) {
  }

  public record JoinForm(
    @NotBlank String loginId,
    @NotBlank String password,
    @NotBlank String email
  ) {
  }

  public record LoginForm(
    @NotBlank String loginId,
    @NotBlank String password
  ) {
  }

  public record FindIdForm(@NotBlank String email) {
  }

  public record FindPasswordForm(
    @NotBlank String loginId,
    @NotBlank String email
  ) {
  }

  public record EditMyPasswordForm(
    @NotBlank String prePassword,
    @NotBlank String newPassword
  ) {
  }

  public record UserQuitForm(
    @NotBlank String loginId,
    @NotBlank String password
  ) {
  }
}
