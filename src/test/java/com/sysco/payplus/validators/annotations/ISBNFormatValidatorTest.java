package com.sysco.payplus.validators.annotations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ISBNFormatValidatorTest {

    @Test
    public void testISBNValidation(){
        ISBNConstraintValidator validator=new ISBNConstraintValidator();
        assertTrue(validator.isValid("ISBN 978-0-596-52068-7",null));
        assertFalse(validator.isValid("ISBN 11978-0-596-52068-7",null));
    }

}