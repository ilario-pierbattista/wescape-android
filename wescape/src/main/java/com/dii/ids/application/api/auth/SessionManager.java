package com.dii.ids.application.api.auth;

public interface SessionManager {
    String getBearer() throws Exception;

    String getAccessToken() throws Exception;
}
