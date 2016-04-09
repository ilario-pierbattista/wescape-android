package com.dii.ids.application.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EmailValidatorTest {
    private EmailValidator validator;
    private final String[] valid = {
            "prova@email"
    };
    private final String[] invalid = {
            "",
            "email"
    };

    @Before
    public void setup() {
        validator = new EmailValidator();
    }

    @Test
    public void testIsValid() {
        for (String validEmail :
                valid) {
            Assert.assertTrue(validator.isValid(validEmail));
        }
        for (String invalidEmail :
                invalid) {
            Assert.assertFalse(validator.isValid(invalidEmail));
        }
    }
}