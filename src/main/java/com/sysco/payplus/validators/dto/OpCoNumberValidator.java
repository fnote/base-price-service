package com.sysco.payplus.validators.dto;

import static com.sysco.payplus.service.exception.ErrorCode.VALIDATION_FAILURE;

import com.sysco.payplus.dto.ErrorDTO;
import com.sysco.payplus.dto.masterdata.BaseOpCoDTO;
import com.sysco.payplus.service.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;

public class OpCoNumberValidator extends DTOValidatorImpl<BaseOpCoDTO> {
  private String opCoNumber;

  public OpCoNumberValidator(String opCoNumber) {
    super("OpCoNumber does not match with the body");
    this.opCoNumber = opCoNumber;
  }

  @Override
  public Void validate(BaseOpCoDTO dto) throws ValidationException {
    if (!this.opCoNumber.equals(dto.getOpCoNumber())) {
      Map<String, String> errors = new HashMap<>();
      errors.put("opCoNumber", getMessage());
      throw new ValidationException(getMessage(), new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), dto, errors));
    }
    super.validate(dto);
    return null;
  }
}
