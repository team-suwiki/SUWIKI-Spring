package usw.suwiki.domain.user.blacklist;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usw.suwiki.infra.jpa.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "createDate", column = @Column(name = "created_at"))
@AttributeOverride(name = "modifiedDate", column = @Column(name = "updated_at"))
public class BlacklistDomain extends BaseEntity {
  @Column
  private Long userIdx;

  @Column
  private String hashedEmail;

  @Column
  private String bannedReason;

  @Column
  private String judgement;

  @Column
  private LocalDateTime expiredAt;
}
