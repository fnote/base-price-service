package com.sysco.payplus.validators.annotations;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OpCoNumberFormatConstraintValidator implements ConstraintValidator<ValidOpCoNumberFormat, String> {

  private Pattern pattern;

  @Override
  public void initialize(ValidOpCoNumberFormat constraintAnnotation) {
    this.pattern = Pattern.compile(constraintAnnotation.pattern());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return (value.length() == 6) && pattern.matcher(value).matches();
  }
}