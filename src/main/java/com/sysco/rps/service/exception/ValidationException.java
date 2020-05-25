package com.sysco.rps.service.exception;

import com.sysco.rps.dto.ErrorDTO;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/1/20 Time: 12:54 PM
 */

public class ValidationException extends Exception {

  private final transient ErrorDTO errorDTO;

  public ValidationException(String message) {
    super(message);
    errorDTO = null;
  }

  public ValidationException(String message, ErrorDTO errorDTO) {
    super(message);
    this.errorDTO = errorDTO;
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }
}
