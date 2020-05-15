package com.sysco.payplus.validators.dto;

import static com.sysco.payplus.service.exception.ErrorCode.VALIDATION_FAILURE;

import com.sysco.payplus.dto.ErrorDTO;
import com.sysco.payplus.dto.masterdata.OpCoDTO;
import com.sysco.payplus.service.exception.ValidationException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public class OpCoDTOValidator extends DTOValidatorImpl<OpCoDTO> {

  private static final String ERROR_MESSAGE_OPCO_NUMBER_SUS_ID_MISMATCH = "OpCo Number and SUS Entity ID doesn't match";
  private static final String ERROR_MESSAGE_OPCO_VALIDATION_FAILURE = "Validation failures for the OpCo with OpCo Number {0}";

  private static final String OPCO_NUMBER_FIELD_NAME = "opCoNumber";
  private static final String SUS_ENTITY_ID_FIELD_NAME = "susEntityId";

  private final Validator validator;

  public OpCoDTOValidator(Validator validator) {
    super("Validation failures for the given OpCo record");
    this.validator = validator;
  }

  @Override
  public Void validate(OpCoDTO opCoDTO) throws ValidationException {
    Set<ConstraintViolation<OpCoDTO>> violations = validator.validate(opCoDTO);
    Map<String, String> errors = new HashMap<>();
    if (!violations.isEmpty()) {
      violations.iterator().forEachRemaining(violation -> {
        String property = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        errors.put(property, message);
      });
    }

    if (!errors.containsKey(OPCO_NUMBER_FIELD_NAME) && !errors.containsKey(SUS_ENTITY_ID_FIELD_NAME) &&
        !checkOpCoNumberMatchWithSusEntityId(opCoDTO.getOpCoNumber(), opCoDTO.getSusEntityId())) {
      errors.put(SUS_ENTITY_ID_FIELD_NAME, ERROR_MESSAGE_OPCO_NUMBER_SUS_ID_MISMATCH);
    }

    if (!errors.isEmpty()) {
      ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), opCoDTO, errors);
      throw new ValidationException(MessageFormat.format(ERROR_MESSAGE_OPCO_VALIDATION_FAILURE, opCoDTO.getOpCoNumber()), errorDTO);
    }
    return super.validate(opCoDTO);
  }

  private boolean checkOpCoNumberMatchWithSusEntityId(String opCoNumber, Integer susEntityId) {
    return susEntityId == Integer.parseInt(opCoNumber.substring(2));
  }
}
