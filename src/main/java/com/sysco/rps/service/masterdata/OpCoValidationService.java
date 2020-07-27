package com.sysco.rps.service.masterdata;

import com.sysco.rps.dto.masterdata.OpCoDTO;
import com.sysco.rps.service.exception.ValidationException;
import com.sysco.rps.validators.dto.DataValidator;
import com.sysco.rps.validators.dto.OpCoDTOValidator;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OpCoValidationService {

  public final DataValidator<OpCoDTO, Void, ValidationException> opCoDTOValidator;

  @Autowired
  public OpCoValidationService(@Qualifier("CustomValidator") Validator validator) {
    this.opCoDTOValidator = new OpCoDTOValidator(validator);
  }
}
