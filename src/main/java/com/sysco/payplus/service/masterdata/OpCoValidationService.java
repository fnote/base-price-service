package com.sysco.payplus.service.masterdata;

import com.sysco.payplus.dto.masterdata.OpCoDTO;
import com.sysco.payplus.service.exception.ValidationException;
import com.sysco.payplus.validators.dto.DataValidator;
import com.sysco.payplus.validators.dto.OpCoDTOValidator;
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
