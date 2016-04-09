package com.dii.ids.application.validators;

public class EmailValidator implements Validator {
    @Override
    public boolean isValid(String email) {
        // @TODO Modificare con una logica più forte
        return !email.isEmpty() && email.contains("@");
    }
}
