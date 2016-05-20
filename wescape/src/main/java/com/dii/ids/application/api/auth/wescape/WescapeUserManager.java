package com.dii.ids.application.api.auth.wescape;

import android.content.Context;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.WescapeErrorCodes;
import com.dii.ids.application.api.auth.Client;
import com.dii.ids.application.api.auth.UserManager;
import com.dii.ids.application.api.auth.exception.DuplicatedEmailException;
import com.dii.ids.application.api.form.ClientForm;
import com.dii.ids.application.api.form.CreateUserForm;
import com.dii.ids.application.api.form.RequestPasswordForm;
import com.dii.ids.application.api.response.StatusResponse;
import com.dii.ids.application.api.response.UserResponse;
import com.dii.ids.application.api.service.WescapeService;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Response;

public class WescapeUserManager implements UserManager {
    private WescapeService service;
    private Client client;

    public WescapeUserManager(Context context) {
        service = ApiBuilder.buildWescapeService(context);
        client = new WescapeClient(context);
    }

    @Override
    public void signup(String email, String password) throws Exception {
        CreateUserForm createUserForm = new CreateUserForm();
        createUserForm.setClient(createClientForm())
                .setEmail(email)
                .setPlainPassword(password);

        Call<UserResponse> call = service.createUser(createUserForm);
        Response<UserResponse> response = call.execute();

        switch (response.code()) {
            case HttpURLConnection.HTTP_CREATED:
                break;
            case WescapeErrorCodes.SIGNUP_DUPLICATED_EMAIL:
                throw new DuplicatedEmailException();
            default:
                throw new Exception();
        }
    }

    @Override
    public void requestSecretCode(String email) throws Exception {
        RequestPasswordForm requestPasswordForm = new RequestPasswordForm();
        requestPasswordForm.setClient(createClientForm())
                .setEmail(email);
        Call<StatusResponse> call = service.requestPasswordReset(requestPasswordForm);
        Response<StatusResponse> response = call.execute();

        if(response.code() != HttpURLConnection.HTTP_ACCEPTED) {
            throw new Exception();
        }
    }

    @Override
    public void resetPassword(String email, String secretCode, String password) throws Exception {

    }

    private ClientForm createClientForm() {
        ClientForm form = new ClientForm();
        form.setId(client.getId())
                .setSecret(client.getSecret());
        return form;
    }
}
