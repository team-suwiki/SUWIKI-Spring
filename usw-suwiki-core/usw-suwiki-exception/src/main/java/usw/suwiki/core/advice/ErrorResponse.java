package usw.suwiki.core.advice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ErrorResponse {
  private final String exception;
  private final String code;
  private final String message;
  private final Integer status;
  private final String error;
}
