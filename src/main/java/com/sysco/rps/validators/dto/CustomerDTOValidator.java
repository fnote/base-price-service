package com.sysco.rps.validators.dto;

import com.sysco.rps.dto.pp.masterdata.BaseCustomerDTO;
import com.sysco.rps.service.exception.ValidationException;
import com.sysco.rps.util.CustomValidationUtil;
import javax.validation.Validator;

public class CustomerDTOValidator extends DTOValidatorImpl<BaseCustomerDTO> {

  private final Validator validator;

  public CustomerDTOValidator(Validator validator) {
    super(null);
    this.validator = validator;
  }

  @Override
  public Void validate(BaseCustomerDTO customerDTO) throws ValidationException {
    CustomValidationUtil.validateDTOConstraints(customerDTO, "CustomerDTO validation failure", validator);
    return super.validate(customerDTO);
  }
}
