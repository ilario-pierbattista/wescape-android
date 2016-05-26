package com.dii.ids.application.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PasswordValidatorTest {
    public static final String[] validPasswords = {
            "aaaaa",
            "12adfd"
    };
    public static final String[] invalidPasswords = {
            "a",
            "",
            "aaaa"
    };
    private PasswordValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new PasswordValidator();
    }

    @Test
    public void testIsValid() {
        for (String valid :
                validPasswords) {
            Assert.assertTrue(validator.isValid(valid));
        }
        for (String invalid :
                invalidPasswords) {
            Assert.assertFalse(validator.isValid(invalid));
        }
    }
}