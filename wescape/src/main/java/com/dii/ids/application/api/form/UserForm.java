package com.dii.ids.application.api.form;

public class UserForm {
    private int id;
    private String email;
    private String device_key;

    public int getId() {
        return id;
    }

    public UserForm setId(int id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserForm setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDevice_key() {
        return device_key;
    }

    public UserForm setDevice_key(String device_key) {
        this.device_key = device_key;
        return this;
    }
}
