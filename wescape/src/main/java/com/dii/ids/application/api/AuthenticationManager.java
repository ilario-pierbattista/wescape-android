package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dii.ids.application.api.response.AccessTokenBundle;

import java.util.Calendar;

public class AuthenticationManager {
    public static final String TAG = AuthenticationManager.class.getName();

    public static final String META_CLIENT_ID_KEY = "com.dii.ids.application.WESCAPE_CLIENT_ID";
    public static final String META_CLIENT_SECRET_KEY = "com.dii.ids.application.WESCAPE_CLIENT_SECRET";
    public static final String ACCESS_TOKEN_KEY = AuthenticationManager.class.getCanonicalName()
            + ".AccessToken";
    public static final String REFRESH_TOKEN_KEY = AuthenticationManager.class.getCanonicalName()
            + ".RefreshToken";
    public static final String ACCESS_EXPIRATION_DATE = AuthenticationManager.class.getCanonicalName()
            + ".AccessTokenExpirationDate";

    private Context context;
    private SharedPreferences preferences;
    private Bundle metaData;

    public AuthenticationManager(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        try {
            metaData = (context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA
            )).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Errore nel caricare i metadati", e);
        }
    }

    public void saveAccessToken(AccessTokenBundle accessTokenBundle) {
        Calendar calendar = Calendar.getInstance();
        float time = calendar.getTime().getTime(); // Tempo in millisecondi
        float expiration = time + (accessTokenBundle.getExpires_in() * 1000);

        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessTokenBundle.getAccess_token())
                .putString(REFRESH_TOKEN_KEY, accessTokenBundle.getRefresh_token())
                .putFloat(ACCESS_EXPIRATION_DATE, expiration)
                .apply();
    }

    public AccessTokenBundle retrieveAccessToken() {
        AccessTokenBundle accessToken = new AccessTokenBundle();
        accessToken.setAccess_token(preferences.getString(ACCESS_TOKEN_KEY, null));
        accessToken.setRefresh_token(preferences.getString(REFRESH_TOKEN_KEY, null));
        accessToken.setExpiration((int) preferences.getFloat(ACCESS_EXPIRATION_DATE, 0));

        if(accessToken.getAccess_token() == null) {
            return null;
        } else {
            return accessToken;
        }
    }

    public String getValidAccessToken() {
        AccessTokenBundle accessTokenBundle = retrieveAccessToken();
        Calendar calendar = Calendar.getInstance();
        float currentTime = calendar.getTimeInMillis();

        if(accessTokenBundle == null) {
            return null;
        } else if(currentTime >= accessTokenBundle.getExpiration()) {
            return null;
        } else {
            return accessTokenBundle.getAccess_token();
        }
    }

    public String getClientId() {
        return metaData.getString(META_CLIENT_ID_KEY);
    }

    public String getClientSecret() {
        return metaData.getString(META_CLIENT_SECRET_KEY);
    }
}
