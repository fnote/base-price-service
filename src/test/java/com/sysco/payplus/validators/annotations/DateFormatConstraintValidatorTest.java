package com.sysco.payplus.validators.annotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DateFormatConstraintValidatorTest {

  @Test
  void isValid() {
    DateFormatConstraintValidator dateFormatConstraintValidator = new DateFormatConstraintValidator();
    dateFormatConstraintValidator.setPattern("MM-dd-yyyy");
    assertTrue(dateFormatConstraintValidator.isValid("10-31-2010", null));
    assertTrue(dateFormatConstraintValidator.isValid(null, null));
    assertFalse(dateFormatConstraintValidator.isValid("10-32-2010", null));
    assertTrue(dateFormatConstraintValidator.isValid("02-29-2020", null));
    assertFalse(dateFormatConstraintValidator.isValid("02-29-2019", null));
    assertFalse(dateFormatConstraintValidator.isValid("13-02-2019", null));
    assertFalse(dateFormatConstraintValidator.isValid("11/02/2019", null));
    assertTrue(dateFormatConstraintValidator.isValid("1-2-2019", null));
  }
}