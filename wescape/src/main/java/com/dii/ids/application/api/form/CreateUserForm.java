package com.dii.ids.application.api.form;

public class CreateUserForm {
    private String email;
    private String plainPassword;
    private ClientForm client;

    public String getEmail() {
        return email;
    }

    public CreateUserForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public CreateUserForm setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
        return this;
    }

    public ClientForm getClient() {
        return client;
    }

    public CreateUserForm setClient(ClientForm client) {
        this.client = client;
        return this;
    }
}
