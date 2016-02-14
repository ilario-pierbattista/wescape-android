package com.dii.ids.application.validators;

import org.apache.commons.lang3.StringUtils;

public class SecretCodeValidator implements Validator {
    @Override
    public boolean isValid(String secretCode) {
        return secretCode.length() > 4 && StringUtils.isNumeric(secretCode);
    }
}
