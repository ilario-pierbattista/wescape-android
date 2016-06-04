package com.dii.ids.application.api.auth.wescape;

import android.content.Context;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.Authenticator;
import com.dii.ids.application.api.auth.Client;
import com.dii.ids.application.api.form.LoginForm;
import com.dii.ids.application.api.response.TokenResponse;
import com.dii.ids.application.api.service.WescapeService;

import retrofit2.Call;
import retrofit2.Response;

public class WescapeAuthenticator implements Authenticator {
    private TokenStorage tokenStorage;
    private WescapeService service;
    private Client client;

    public WescapeAuthenticator(Context context, String hostname) {
        tokenStorage = new TokenStorage(context);
        service = ApiBuilder.buildWescapeService(hostname);
        client = new WescapeClient(context);
    }

    @Override
    public void login(String email, String password) throws Exception {
        LoginForm form = new LoginForm();
        form.setClient_id(client.getId())
                .setClient_secret(client.getSecret())
                .setUsername(email)
                .setPassword(password);

        Call<TokenResponse> call = service.getAccessToken(form);
        Response<TokenResponse> response = call.execute();
        Token token = new Token(response.body());

        tokenStorage.save(token);
    }

    @Override
    public void logout() throws Exception {
        tokenStorage.delete();
    }
}
