package com.sysco.payplus.service.masterdata;

import com.sysco.payplus.dto.masterdata.BaseCustomerDTO;
import com.sysco.payplus.service.exception.ValidationException;
import com.sysco.payplus.validators.dto.CustomerDTOValidator;
import com.sysco.payplus.validators.dto.DataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Service
public class CustomerValidationService {

  public final DataValidator<BaseCustomerDTO, Void, ValidationException> customerDTOValidator;

  @Autowired
  public CustomerValidationService(@Qualifier("CustomValidator") Validator validator) {
    this.customerDTOValidator = new CustomerDTOValidator(validator);
  }
}
