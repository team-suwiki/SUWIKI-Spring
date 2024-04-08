package usw.suwiki.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeRequest {

  @Data
  public static class Create {
    @NotBlank
    private final String title;

    @NotBlank
    private final String content;
  }

  @Data
  public static class Update {
    @NotBlank
    private final String title;

    @NotBlank
    private final String content;
  }
}
