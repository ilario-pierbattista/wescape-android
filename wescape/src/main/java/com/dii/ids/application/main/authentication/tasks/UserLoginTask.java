package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.main.authentication.LoginFragment;
import com.dii.ids.application.api.response.AccessTokenResponse;
import com.dii.ids.application.api.form.PasswordOAuth2Form;
import com.dii.ids.application.api.service.OAuth2Service;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = UserLoginTask.class.getName();

    /**
     * A dummy authentication store containing known user names and passwords. TODO: remove after
     * connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private final String email;
    private final String password;
    private LoginFragment fragment;

    public UserLoginTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserLoginTask inject(LoginFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
            // @TODO Sistemare
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.42.0.1")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            OAuth2Service wescapeService = retrofit.create(OAuth2Service.class);
            PasswordOAuth2Form passwordOAuth2Form = new PasswordOAuth2Form();
            passwordOAuth2Form.setClient_id("2_f9333e7fd031066729f232e7a1d3ceed622605a0317386339915a04b7fb3bcd1");
            passwordOAuth2Form.setClient_secret("03d6630b54ff78bb1e616994f60ccb11b9c7547ea3fe25534e4afc944537e14d");
            passwordOAuth2Form.setUsername("admin");
            passwordOAuth2Form.setPassword("admin");

            Call<AccessTokenResponse> call = wescapeService.getAccessToken(passwordOAuth2Form);
            Log.i(TAG, "Pre execute");
            Response<AccessTokenResponse> response = call.execute();
            Log.i(TAG, "Post execute");
            AccessTokenResponse accessToken = response.body();
            Log.i(TAG, "Response " + accessToken.getAccess_token());
            Log.i(TAG, "Response " + accessToken.getRefresh_token());
            Log.i(TAG, "Response " + accessToken.getExpires_in());

        } catch (IOException e) {
            Log.e(TAG, "Login Error", e);
            return false;
        } catch (InterruptedException e) {
            return false;
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(email)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(password);
            }
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            fragment.onTaskSuccess(this);
        } else {
            fragment.onTaskCancelled(this);
        }
    }

    @Override
    protected void onCancelled() {
        fragment.onTaskCancelled(this);
    }
}