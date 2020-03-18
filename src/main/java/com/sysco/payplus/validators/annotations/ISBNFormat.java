package com.sysco.payplus.validators.annotations;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@Documented
@Constraint(validatedBy = ISBNConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

public @interface ISBNFormat {
    String message() default "ISBN format validation failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}