package com.dii.ids.application.api.auth;

public interface UserManager {
    void signup(String email, String password);

    void requestSecretCode(String email);

    void resetPassword(String email, String secretCode, String password);
}
