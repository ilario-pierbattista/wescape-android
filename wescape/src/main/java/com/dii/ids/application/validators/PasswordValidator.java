package com.dii.ids.application.validators;

import android.text.TextUtils;

public class PasswordValidator implements Validator {
    @Override
    public boolean isValid(String password) {
        // @TODO Aggiungere una logica più forte
        return !TextUtils.isEmpty(password) && password.length() > 4;
    }
}
