package com.sysco.payplus.service.exception;

import com.sysco.payplus.dto.ErrorDTO;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/24/20 Time: 12:54 PM
 */

public class DuplicateRecordException extends Exception {

  private final transient ErrorDTO errorDTO;

  public DuplicateRecordException(String message) {
    super(message);
    this.errorDTO = null;
  }

  public DuplicateRecordException(String message, ErrorDTO errorDTO) {
    super(message);
    this.errorDTO = errorDTO;
  }

  public ErrorDTO getErrorDTO() {
    return errorDTO;
  }
}
