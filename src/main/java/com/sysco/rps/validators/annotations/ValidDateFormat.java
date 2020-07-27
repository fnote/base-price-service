package com.sysco.rps.validators.annotations;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA. Author: rohana.kumara@sysco.com Date: 3/13/20 Time: 12:54 PM
 */
@Documented
@Constraint(validatedBy = DateFormatConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {

  String message() default "Date validation failed.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String pattern();
}
