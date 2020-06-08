package com.sysco.rps.validators.dto;

import com.sysco.rps.dto.pp.masterdata.BaseGlobalDTO;
import com.sysco.rps.service.exception.ValidationException;

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
