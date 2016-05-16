package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dii.ids.application.api.response.AccessTokenResponse;

import java.util.Calendar;

public class AuthenticationManager {
    public static final String ACCESS_TOKEN_KEY = AuthenticationManager.class.getCanonicalName()
            + ".AccessToken";
    public static final String REFRESH_TOKEN_KEY = AuthenticationManager.class.getCanonicalName()
            + ".RefreshToken";
    public static final String ACCESS_EXPIRATION_DATE = AuthenticationManager.class.getCanonicalName()
            + ".AccessTokenExpirationDate";

    private Context context;
    private SharedPreferences preferences;

    public AuthenticationManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public void saveAccessToken(AccessTokenResponse accessTokenResponse) {
        Calendar calendar = Calendar.getInstance();
        float time = calendar.getTime().getTime(); // Tempo in millisecondi
        float expiration = time + (accessTokenResponse.getExpires_in() * 1000);

        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessTokenResponse.getAccess_token())
                .putString(REFRESH_TOKEN_KEY, accessTokenResponse.getRefresh_token())
                .putFloat(ACCESS_EXPIRATION_DATE, expiration)
                .apply();
    }
}
