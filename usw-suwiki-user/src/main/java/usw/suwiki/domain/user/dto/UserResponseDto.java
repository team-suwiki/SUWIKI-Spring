package usw.suwiki.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import usw.suwiki.domain.user.User;

import java.time.LocalDateTime;

public final class UserResponseDto {

  @Builder
  public record UserInformationResponseForm(
    String loginId,
    String email,
    Integer point,
    Integer writtenEvaluation,
    Integer writtenExam,
    Integer viewExam
  ) {
    public static UserInformationResponseForm toMyPageResponse(User user) {
      return builder()
        .loginId(user.getLoginId())
        .email(user.getEmail())
        .point(user.getPoint())
        .writtenEvaluation(user.getWrittenEvaluation())
        .writtenExam(user.getWrittenExam())
        .viewExam(user.getViewExamCount())
        .build();
    }
  }

  @Builder
  public record LoadMyRestrictedReasonResponseForm(
    String restrictedReason,
    String judgement,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime restrictingDate
  ) {
  }

  @Builder
  public record LoadMyBlackListReasonResponseForm(
    String blackListReason,
    String judgement,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime expiredAt
  ) {
  }
}
