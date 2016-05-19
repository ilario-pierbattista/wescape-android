package com.dii.ids.application.api.form;


public class ClientForm {
    private String id;
    private String secret;

    public String getId() {
        return id;
    }

    public ClientForm setId(String id) {
        this.id = id;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public ClientForm setSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
