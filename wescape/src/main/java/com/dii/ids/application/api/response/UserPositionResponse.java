package com.dii.ids.application.api.response;


import com.dii.ids.application.entity.Edge;

public class UserPositionResponse {
    private int id;
    private UserResponse user;
    private Edge edge;

    public int getId() {
        return id;
    }

    public UserPositionResponse setId(int id) {
        this.id = id;
        return this;
    }

    public UserResponse getUser() {
        return user;
    }

    public UserPositionResponse setUser(UserResponse user) {
        this.user = user;
        return this;
    }

    public Edge getEdge() {
        return edge;
    }

    public UserPositionResponse setEdge(Edge edge) {
        this.edge = edge;
        return this;
    }
}
