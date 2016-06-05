package com.dii.ids.application.api.form;


public class UserPositionForm {
    private int id;
    private int user;
    private int edge;

    public int getId() {
        return id;
    }

    public UserPositionForm setId(int id) {
        this.id = id;
        return this;
    }

    public int getUser() {
        return user;
    }

    public UserPositionForm setUser(int user) {
        this.user = user;
        return this;
    }

    public int getEdge() {
        return edge;
    }

    public UserPositionForm setEdge(int edge) {
        this.edge = edge;
        return this;
    }
}
