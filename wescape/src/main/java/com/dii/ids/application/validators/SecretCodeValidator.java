package com.dii.ids.application.validators;

import org.apache.commons.lang3.StringUtils;

public class SecretCodeValidator implements Validator {
    @Override
    public boolean isValid(String secretCode) {
        return secretCode.length() == 6 && StringUtils.isAlphanumeric(secretCode);
    }
}
