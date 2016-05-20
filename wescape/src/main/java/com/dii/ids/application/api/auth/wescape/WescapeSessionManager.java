package com.dii.ids.application.api.auth.wescape;

import android.content.Context;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.Client;
import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.auth.exception.TokenNotFoundException;
import com.dii.ids.application.api.form.RefreshTokenForm;
import com.dii.ids.application.api.response.TokenResponse;
import com.dii.ids.application.api.service.WescapeService;

import retrofit2.Call;
import retrofit2.Response;

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
    public String getBearer() throws Exception {
        keepTokenUpdated();
        Token token = tokenStorage.get();
        String bearer = null;
        if (token != null) {
            bearer = "Bearer " + token.getAccessToken();
        }
        return bearer;
    }

    @Override
    public String getAccessToken() throws Exception {
        keepTokenUpdated();
        String accessToken = null;
        if (tokenStorage.get() != null) {
            accessToken = tokenStorage.get().getAccessToken();
        }
        return accessToken;
    }

    /**
     * Mantiene il token aggiornato
     * @throws Exception
     */
    private void keepTokenUpdated() throws Exception {
        Token currentToken = tokenStorage.get();
        if(currentToken == null) {
            throw new TokenNotFoundException();
        } else if(currentToken.isExpired()) {
            RefreshTokenForm refreshForm = new RefreshTokenForm();

            refreshForm.setClient_id(client.getId())
                    .setClient_secret(client.getSecret())
                    .setRefresh_token(currentToken.getRefreshToken());

            Call<TokenResponse> call = service.refreshAccessToken(refreshForm);
            Response<TokenResponse> response = call.execute();
            Token token = new Token(response.body());

            tokenStorage.save(token);
        }
    }
}
