package com.dii.ids.application.api.auth;

public interface SessionManager {
    String getBearer();

    String getAccessToken();
}
