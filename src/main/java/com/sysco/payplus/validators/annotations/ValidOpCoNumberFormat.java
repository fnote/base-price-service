package com.sysco.payplus.validators.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = OpCoNumberFormatConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOpCoNumberFormat {

  String message() default "OpCo Number validation failed.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String pattern() default "[A-Z]{2}\\d{4}";
}