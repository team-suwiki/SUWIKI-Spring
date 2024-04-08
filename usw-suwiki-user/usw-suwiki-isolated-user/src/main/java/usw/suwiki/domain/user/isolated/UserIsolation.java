package usw.suwiki.domain.user.isolated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.core.secure.PasswordEncoder;
import usw.suwiki.core.secure.RandomPasswordGenerator;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIsolation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long userIdx;

  @Column
  private String loginId;

  @Column
  private String password;

  @Column
  private String email;

  @Column
  private LocalDateTime requestedQuitDate;

  @Column
  private LocalDateTime lastLogin;

  public boolean validatePassword(PasswordEncoder passwordEncoder, String inputPassword) {
    return passwordEncoder.matches(inputPassword, password);
  }

  public String updateRandomPassword(PasswordEncoder passwordEncoder) {
    String newPassword = RandomPasswordGenerator.generate();
    this.password = passwordEncoder.encode(newPassword);
    return newPassword;
  }
}
