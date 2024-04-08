package usw.suwiki.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@JsonInclude(Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
  private final String code;
  private final T data;
  private final String message;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(null, data, null);
  }

  public static ApiResponse<?> success() {
    return new ApiResponse<>(null, Map.of("success", true), null);
  }
}
