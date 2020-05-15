package com.sysco.payplus.validators.dto;

import com.sysco.payplus.dto.masterdata.BaseGlobalDTO;
import com.sysco.payplus.service.exception.ValidationException;

public abstract class DTOValidatorImpl<T extends BaseGlobalDTO> implements DataValidator<T, Void, ValidationException> {
  private DTOValidatorImpl next;
  private String message;

  DTOValidatorImpl(String message) {
    this.message = message;
  }

  public DTOValidatorImpl setNext(DTOValidatorImpl next) {
    this.next = next;
    return this.next;
  }

  public Void validate(T dto) throws ValidationException {
    if (this.next != null) {
      return this.next.validate(dto);
    }
    return null;
  }

  public String getMessage() {
    return message;
  }
}
