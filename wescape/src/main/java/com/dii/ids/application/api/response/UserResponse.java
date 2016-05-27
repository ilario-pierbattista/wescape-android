package com.dii.ids.application.api.response;


public class UserResponse {
    private int id;
    private String email;

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
}
