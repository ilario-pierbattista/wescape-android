package com.dii.ids.application.api.form;


public class ResetPasswordForm {
    private String email;
    private String reset_password_token;
    private String new_password;
    private ClientForm client;

    public String getEmail() {
        return email;
    }

    public ResetPasswordForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getReset_password_token() {
        return reset_password_token;
    }

    public ResetPasswordForm setReset_password_token(String reset_password_token) {
        this.reset_password_token = reset_password_token;
        return this;
    }

    public String getNew_password() {
        return new_password;
    }

    public ResetPasswordForm setNew_password(String new_password) {
        this.new_password = new_password;
        return this;
    }

    public ClientForm getClient() {
        return client;
    }

    public ResetPasswordForm setClient(ClientForm client) {
        this.client = client;
        return this;
    }
}
