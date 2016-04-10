package com.dii.ids.application.validators;

public interface Validator {
    /**
     * Convalida il contenuto di una stringa
     *
     * @param text Stringa da convalidare
     * @return True se text è valido, False altrimenti
     */
    boolean isValid(String text);
}
