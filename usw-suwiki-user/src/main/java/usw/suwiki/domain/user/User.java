package usw.suwiki.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.auth.token.ConfirmationToken;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.core.secure.PasswordRandomizer;
import usw.suwiki.infra.jpa.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Optional;

import static usw.suwiki.core.exception.ExceptionType.EMAIL_NOT_AUTHED;
import static usw.suwiki.core.exception.ExceptionType.USER_POINT_LACK;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createDate", column = @Column(name = "created_at"))
@AttributeOverride(name = "modifiedDate", column = @Column(name = "updated_at"))
public class User extends BaseEntity {
  private static final int DELETE_POINT_LIMIT = 30;
  private static final int PURCHASE_POINT_LIMIT = 20;
  private static final int WROTE_EVALUATION_BONUS = 10;

  @Column
  private String loginId;

  @Column
  private String password;

  @Column
  private String email;

  @Column
  private Boolean restricted;

  @Column
  private Integer restrictedCount;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum") // todo: flyway 수정 후 삭제
  private Role role;

  @Column
  private Integer writtenEvaluation;

  @Column
  private Integer writtenExam;

  @Column
  private Integer viewExamCount;

  @Column
  private Integer point;

  @Column
  private LocalDateTime lastLogin;

  @Column
  private LocalDateTime requestedQuitDate;

  public static User init(String loginId, String password, String email) {
    return builder()
      .loginId(loginId)
      .password(password)
      .email(email)
      .restricted(true)
      .restrictedCount(0)
      .writtenEvaluation(0)
      .writtenExam(0)
      .point(0)
      .viewExamCount(0)
      .build();
  }

  public void restricted() {
    this.restricted = true;
  }

  public void released() {
    this.restricted = false;
  }

  public void waitQuit() {
    this.restrictedCount = null;
    this.role = null;
    this.writtenExam = null;
    this.writtenEvaluation = null;
    this.viewExamCount = null;
    this.point = null;
    this.lastLogin = null;
    this.requestedQuitDate = LocalDateTime.now();
    restricted();
  }

  public void sleep() {
    this.loginId = null;
    this.password = null;
    this.email = null;
  }

  public void awake(String loginId, String password, String email) {
    this.loginId = loginId;
    this.password = password;
    this.email = email;
    login();
  }

  public void activateUser() {
    this.restricted = false;
    this.role = Role.USER;
    super.modified();
  }

  public boolean isAdmin() {
    return this.role == Role.ADMIN;
  }

  public void updatePassword(PasswordEncoder passwordEncoder, String newPassword) {
    this.password = passwordEncoder.encode(newPassword);
  }

  public String updateRandomPassword(PasswordEncoder passwordEncoder) {
    String generatedPassword = PasswordRandomizer.randomizePassword();
    this.password = passwordEncoder.encode(generatedPassword);
    return generatedPassword;
  }

  public boolean validatePassword(PasswordEncoder passwordEncoder, String inputPassword) {
    return passwordEncoder.matches(inputPassword, this.password);
  }

  public boolean isUserEmailAuthed(Optional<ConfirmationToken> confirmationToken) {
    if (confirmationToken.isEmpty()) {
      throw new AccountException(EMAIL_NOT_AUTHED);
    }

    if (confirmationToken.get().isVerified()) {
      return true;
    }

    throw new AccountException(EMAIL_NOT_AUTHED);
  }

  public void login() {
    this.lastLogin = LocalDateTime.now();
  }

  public void writeEvaluatePost() {
    this.point += WROTE_EVALUATION_BONUS;
    this.writtenEvaluation += 1;
  }

  public void deleteEvaluatePost() {
    validatePointLimit(PURCHASE_POINT_LIMIT);
    this.point -= DELETE_POINT_LIMIT;
    this.writtenEvaluation -= 1;
  }

  public void writeExamPost() {
    this.point += 20;
    this.writtenExam += 1;
  }

  public void purchaseExamPost() {
    validatePointLimit(PURCHASE_POINT_LIMIT);
    this.point -= PURCHASE_POINT_LIMIT;
    this.viewExamCount += 1;
  }

  public void deleteExamPost() {
    validatePointLimit(DELETE_POINT_LIMIT);
    this.point -= DELETE_POINT_LIMIT;
    this.writtenExam -= 1;
  }

  private void validatePointLimit(int required) {
    if (this.point < required) {
      throw new AccountException(USER_POINT_LACK);
    }
  }

  public void increaseRestrictedCountByReportedPost() {
    this.restrictedCount += 1;
  }

  public void increasePointByReporting() {
    this.point += 1;
  }
}
