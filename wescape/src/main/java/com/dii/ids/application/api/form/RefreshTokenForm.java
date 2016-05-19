package com.dii.ids.application.api.form;

public class RefreshTokenForm {
    private final String grant_type = "refresh_token";
    private String client_id;
    private String client_secret;
    private String refresh_token;

    public String getClient_id() {
        return client_id;
    }

    public RefreshTokenForm setClient_id(String client_id) {
        this.client_id = client_id;
        return this;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public RefreshTokenForm setClient_secret(String client_secret) {
        this.client_secret = client_secret;
        return this;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public RefreshTokenForm setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
        return this;
    }
}
