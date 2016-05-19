package com.dii.ids.application.api.auth.wescape;

import android.content.Context;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.Client;
import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.service.WescapeService;

public class WescapeSessionManager implements SessionManager {
    private TokenStorage tokenStorage;
    private Client client;
    private WescapeService service;

    public WescapeSessionManager(Context context) {
        tokenStorage = new TokenStorage(context);
        client = new WescapeClient(context);
        service = ApiBuilder.buildWescapeService(context);
    }

    @Override
    public String getBearer() {
        Token token = tokenStorage.get();
        String bearer = null;
        if (token != null) {
            bearer = "Bearer " + token.getAccessToken();
        }
        return bearer;
    }

    @Override
    public String getAccessToken() {
        String accessToken = null;
        if (tokenStorage.get() != null) {
            accessToken = tokenStorage.get().getAccessToken();
        }
        return accessToken;
    }
}
