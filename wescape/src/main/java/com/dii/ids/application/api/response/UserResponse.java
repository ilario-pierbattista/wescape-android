package com.dii.ids.application.api.response;


public class UserResponse {
    private int id;
    private String email;
    private String device_key;

    public int getId() {
        return id;
    }

    public UserResponse setId(int id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDevice_key() {
        return device_key;
    }

    public UserResponse setDevice_key(String device_key) {
        this.device_key = device_key;
        return this;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", device_key='" + device_key + '\'' +
                '}';
    }
}
