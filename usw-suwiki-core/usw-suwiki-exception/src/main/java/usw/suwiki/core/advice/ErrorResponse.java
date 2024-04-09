package usw.suwiki.core.advice;

import usw.suwiki.core.exception.ExceptionType;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public record ErrorResponse(String code, String message, Integer status) {

  static ErrorResponse from(ExceptionType exceptionType) {
    return new ErrorResponse(exceptionType.getCode(), exceptionType.getMessage(), exceptionType.getStatus());
  }

  static ErrorResponse internal(String message) {
    return new ErrorResponse(INTERNAL_SERVER_ERROR.name(), message, INTERNAL_SERVER_ERROR.value());
  }
}
