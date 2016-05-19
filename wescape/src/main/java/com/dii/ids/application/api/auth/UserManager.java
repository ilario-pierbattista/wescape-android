package com.dii.ids.application.api.auth;

public interface UserManager {
    void signup(String email, String password) throws Exception;

    void requestSecretCode(String email) throws Exception;

    void resetPassword(String email, String secretCode, String password) throws Exception;
}
