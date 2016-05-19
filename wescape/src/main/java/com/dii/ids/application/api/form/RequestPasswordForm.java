package com.dii.ids.application.api.form;


public class RequestPasswordForm {
    private String email;
    private ClientForm client;

    public String getEmail() {
        return email;
    }

    public RequestPasswordForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public ClientForm getClient() {
        return client;
    }

    public RequestPasswordForm setClient(ClientForm client) {
        this.client = client;
        return this;
    }
}
