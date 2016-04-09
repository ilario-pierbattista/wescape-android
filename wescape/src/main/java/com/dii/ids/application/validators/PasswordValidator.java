package com.dii.ids.application.validators;

public class PasswordValidator implements Validator {
    @Override
    public boolean isValid(String password) {
        // @TODO Aggiungere una logica più forte
        return !password.isEmpty() && password.length() > 4;
    }
}
