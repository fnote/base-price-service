package com.sysco.rps.util;

import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.dto.masterdata.BaseGlobalDTO;
import com.sysco.rps.dto.masterdata.BaseOpCoDTO;
import com.sysco.rps.service.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.sysco.rps.service.exception.ErrorCode.VALIDATION_FAILURE;

public class CustomValidationUtil {

    private CustomValidationUtil() {}

    public static <T extends BaseGlobalDTO> void validateDTOConstraints(T dto, String validationMessage, Validator validator) throws ValidationException {

        if (Objects.isNull(dto)) {
            throw new ValidationException(validationMessage, new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription()));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            violations.iterator().forEachRemaining(violation -> {
                String property = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                errors.put(property, message);
            });
            ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), dto, errors);
            throw new ValidationException(validationMessage, errorDTO);
        }
    }

    public static <T extends BaseOpCoDTO> void validateOpCoNumberMatchesWithDTO(String opCoNumber, T dto) throws ValidationException {
        if (Objects.isNull(dto.getOpCoNumber()) || !dto.getOpCoNumber().equals(opCoNumber)) {
            String errorMessage = "In consistent opCo number in the given list";
            ErrorDTO errorDTO = new ErrorDTO(VALIDATION_FAILURE.getCode(), VALIDATION_FAILURE.getDescription(), dto, errorMessage);
            throw new ValidationException(errorMessage, errorDTO);
        }
    }
}
