package com.dii.ids.application.api.form;

public class PasswordOAuth2Form {
    private final String grant_type = "password";
    private String client_id;
    private String client_secret;
    private String username;
    private String password;

    public String getClient_id() {
        return client_id;
    }

    public PasswordOAuth2Form setClient_id(String client_id) {
        this.client_id = client_id;
        return this;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public PasswordOAuth2Form setClient_secret(String client_secret) {
        this.client_secret = client_secret;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PasswordOAuth2Form setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public PasswordOAuth2Form setPassword(String password) {
        this.password = password;
        return this;
    }
}
