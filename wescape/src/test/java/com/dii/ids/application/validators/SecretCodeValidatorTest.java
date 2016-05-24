package com.dii.ids.application.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecretCodeValidatorTest {
    public static final String[] validCodes = {
            "4AD60A",
            "AAAAAA",
            "000000"
    };
    public static final String[] invalidCodes = {
            "",
            "a4564",
            "3445",
            "a4"
    };
    private SecretCodeValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new SecretCodeValidator();
    }

    @Test
    public void testIsValid() {
        for (String valid :
                validCodes) {
            Assert.assertTrue(validator.isValid(valid));
        }
        for (String invalid :
                invalidCodes) {
            Assert.assertFalse(validator.isValid(invalid));
        }
    }
}