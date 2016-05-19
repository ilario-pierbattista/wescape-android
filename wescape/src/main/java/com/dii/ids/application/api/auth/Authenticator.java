package com.dii.ids.application.api.auth;

public interface Authenticator {
    void login(String email, String password) throws Exception;
}
