package com.dii.ids.application.api.form;

public class UserForm {
    private int id;
    private String email;
    private String deviceKey;

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

    public String getDeviceKey() {
        return deviceKey;
    }

    public UserForm setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
        return this;
    }
}
