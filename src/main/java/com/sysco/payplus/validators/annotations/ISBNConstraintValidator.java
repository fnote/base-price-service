package com.sysco.payplus.validators.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
public class ISBNConstraintValidator implements ConstraintValidator<ISBNFormat, String> {
    //ISBN pattern from the web
    String regex = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$";
    Pattern pattern = Pattern.compile(regex);

    @Override
    public void initialize(ISBNFormat ISBNFormat) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }
}