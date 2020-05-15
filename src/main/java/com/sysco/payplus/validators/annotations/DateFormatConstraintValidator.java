package com.sysco.payplus.validators.annotations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 4/2/20 Time: 12:54 PM
 */

public class DateFormatConstraintValidator implements ConstraintValidator<ValidDateFormat, String> {

  private String pattern;

  @Override
  public void initialize(ValidDateFormat constraintAnnotation) {
    this.pattern = constraintAnnotation.pattern();
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    try {
      DateFormat sdf = new SimpleDateFormat(pattern);
      sdf.setLenient(false);
      sdf.parse(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}