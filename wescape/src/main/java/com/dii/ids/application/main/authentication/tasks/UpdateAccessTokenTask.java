package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.AuthenticationManager;
import com.dii.ids.application.api.form.RefreshOAuthForm;
import com.dii.ids.application.api.response.AccessTokenBundle;
import com.dii.ids.application.api.service.OAuth2Service;
import com.dii.ids.application.listener.TaskListener;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Response;

public class UpdateAccessTokenTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = UpdateAccessTokenTask.class.getName();

    private TaskListener<AccessTokenBundle> listener;
    private AccessTokenBundle accessTokenBundle;
    private ApiBuilder apiBuilder;
    private AuthenticationManager authenticationManager;

    public UpdateAccessTokenTask(TaskListener<AccessTokenBundle> listener,
                                 ApiBuilder apiBuilder,
                                 AuthenticationManager authenticationManager) {
        this.listener = listener;
        this.apiBuilder = apiBuilder;
        this.authenticationManager = authenticationManager;

        this.accessTokenBundle = authenticationManager.retrieveAccessToken();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Calendar calendar = Calendar.getInstance();
            float currentTime = calendar.getTimeInMillis();

            // Token scaduto
            if (accessTokenBundle != null &&
                     currentTime >= accessTokenBundle.getExpiration()) {
                RefreshOAuthForm refreshOAuthForm = new RefreshOAuthForm();
                refreshOAuthForm.setClient_id(authenticationManager.getClientId());
                refreshOAuthForm.setClient_secret(authenticationManager.getClientSecret());
                refreshOAuthForm.setRefresh_token(accessTokenBundle.getRefresh_token());

                OAuth2Service oAuth2Service = apiBuilder.buildAuthService();
                Call<AccessTokenBundle> call = oAuth2Service.refreshAccessToken(refreshOAuthForm);
                Response<AccessTokenBundle> response = call.execute();

                accessTokenBundle = response.body();
            }

            // Ha successo se si ha a disposizione un token valido
            return (accessTokenBundle != null);
        } catch (IOException e) {
            Log.e(TAG, "Login Error", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(accessTokenBundle);
        } else {
            listener.onTaskError();
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }
}
