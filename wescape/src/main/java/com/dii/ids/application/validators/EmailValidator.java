package com.dii.ids.application.validators;

import android.text.TextUtils;

public class EmailValidator implements Validator {
    @Override
    public boolean isValid(String email) {
        // @TODO Modificare con una logica più forte
        return !TextUtils.isEmpty(email) && email.contains("@");
    }
}
