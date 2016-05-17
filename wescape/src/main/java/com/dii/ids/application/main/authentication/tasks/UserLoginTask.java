package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.response.AccessTokenBundle;
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
    private TaskListener<AccessTokenBundle> listener;
    private ApiBuilder apiBuilder;
    private PasswordOAuth2Form form;
    private AccessTokenBundle accessTokenBundle;

    public UserLoginTask(ApiBuilder apiBuilder,
                         TaskListener<AccessTokenBundle> listener,
                         PasswordOAuth2Form form) {
        this.apiBuilder = apiBuilder;
        this.listener = listener;
        this.form = form;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            OAuth2Service oAuth2Service = apiBuilder.buildAuthService();
            Call<AccessTokenBundle> call = oAuth2Service.getAccessToken(form);
            Response<AccessTokenBundle> response = call.execute();
            accessTokenBundle = response.body();

            return (accessTokenBundle != null);
        } catch (IOException e) {
            Log.e(TAG, "Login Error", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
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