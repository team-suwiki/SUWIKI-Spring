package usw.suwiki.auth.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long userIdx;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column
  private LocalDateTime confirmedAt;

  public ConfirmationToken(Long userIdx) {
    this.userIdx = userIdx;
    this.token = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.expiresAt = LocalDateTime.now().plusMinutes(15);
  }

  public void confirmed() {
    this.confirmedAt = LocalDateTime.now();
  }

  public boolean isTokenExpired() {
    return this.expiresAt.isBefore(LocalDateTime.now());
  }

  public boolean isVerified() {
    return this.confirmedAt != null;
  }
}
