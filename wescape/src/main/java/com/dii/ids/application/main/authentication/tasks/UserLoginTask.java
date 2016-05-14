package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.response.AccessTokenResponse;
import com.dii.ids.application.api.service.OAuth2Service;
import com.dii.ids.application.listener.TaskListener;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UserLoginTask.class.getName();
    private TaskListener<AccessTokenResponse> listener;
    private ApiBuilder apiBuilder;
    private PasswordOAuth2Form form;
    private AccessTokenResponse accessTokenResponse;

    public UserLoginTask(ApiBuilder apiBuilder,
                         TaskListener<AccessTokenResponse> listener,
                         PasswordOAuth2Form form) {
        this.apiBuilder = apiBuilder;
        this.listener = listener;
        this.form = form;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // @TODO Sistemare
            OAuth2Service oAuth2Service = apiBuilder.buildAuthService();
            Call<AccessTokenResponse> call = oAuth2Service.getAccessToken(form);

            Log.i(TAG, "Pre execute");

            Response<AccessTokenResponse> response = call.execute();

            Log.i(TAG, "Post execute");

            accessTokenResponse = response.body();

            Log.i(TAG, "Response " + accessTokenResponse.getAccess_token());
            Log.i(TAG, "Response " + accessTokenResponse.getRefresh_token());
            Log.i(TAG, "Response " + accessTokenResponse.getExpires_in());

        } catch (IOException e) {
            Log.e(TAG, "Login Error", e);
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(accessTokenResponse);
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