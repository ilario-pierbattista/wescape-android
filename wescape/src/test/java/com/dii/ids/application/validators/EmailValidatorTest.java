package com.dii.ids.application.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EmailValidatorTest {
    private EmailValidator validator;
    public static final String[] validEmails = {
            "prova@email"
    };
    public static final String[] invalidEmails = {
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
                validEmails) {
            Assert.assertTrue(validator.isValid(validEmail));
        }
        for (String invalidEmail :
                invalidEmails) {
            Assert.assertFalse(validator.isValid(invalidEmail));
        }
    }
}