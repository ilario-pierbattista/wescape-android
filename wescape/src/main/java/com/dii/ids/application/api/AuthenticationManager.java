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
    public static final String ACCESS_TOKEN_KEY = AuthenticationManager.class.getCanonicalName() + ".AccessToken";
    public static final String REFRESH_TOKEN_KEY = AuthenticationManager.class.getCanonicalName() + ".RefreshToken";
    public static final String ACCESS_EXPIRATION_DATE = AuthenticationManager.class.getCanonicalName() + ".AccessTokenExpirationDate";
    public static final String ACCESS_EXPIRES_IN = AuthenticationManager.class.getCanonicalName() + ".AccessTokenExpiresIn";

    private Context context;
    private SharedPreferences preferences;
    private Bundle metaData;

    /**
     * Costruttore
     *
     * @param context
     */
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

    /**
     * Salvataggio dell'access token
     *
     * @param accessTokenBundle
     */
    public void saveAccessToken(AccessTokenBundle accessTokenBundle) {
        Calendar calendar = Calendar.getInstance();
        float time = calendar.getTimeInMillis(); // Tempo in millisecondi
        accessTokenBundle.setExpiration(time + (accessTokenBundle.getExpires_in() * 1000));

        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessTokenBundle.getAccess_token())
                .putString(REFRESH_TOKEN_KEY, accessTokenBundle.getRefresh_token())
                .putFloat(ACCESS_EXPIRATION_DATE, accessTokenBundle.getExpiration())
                .putInt(ACCESS_EXPIRES_IN, accessTokenBundle.getExpires_in())
                .apply();

        // @TODO Remove
        System.out.println(accessTokenBundle.toString());
    }

    /**
     * Deserializzazione dell'access token
     *
     * @return
     */
    public AccessTokenBundle retrieveAccessToken() {
        AccessTokenBundle accessToken = new AccessTokenBundle();
        accessToken.setAccess_token(preferences.getString(ACCESS_TOKEN_KEY, null));
        accessToken.setRefresh_token(preferences.getString(REFRESH_TOKEN_KEY, null));
        accessToken.setExpires_in(preferences.getInt(ACCESS_EXPIRES_IN, 0));
        accessToken.setExpiration(preferences.getFloat(ACCESS_EXPIRATION_DATE, 0));

        // @TODO Remove
        System.out.println(accessToken.toString());

        if (accessToken.getAccess_token() == null) {
            return null;
        } else {
            return accessToken;
        }
    }

    /**
     * Rimozione dell'access token
     */
    public void deleteAccessToken() {
        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, null)
                .putString(REFRESH_TOKEN_KEY, null)
                .putString(ACCESS_EXPIRATION_DATE, null)
                .putString(ACCESS_EXPIRES_IN, null)
                .apply();
    }

    /**
     * Restituisce un access token valido
     *
     * @return
     */
    public String getValidAccessToken() {
        AccessTokenBundle accessTokenBundle = retrieveAccessToken();

        if (accessTokenBundle == null) {
            return null;
        } else if (accessTokenBundle.isExpired()) {
            return null;
        } else {
            return accessTokenBundle.getAccess_token();
        }
    }

    /**
     * Restituisce il bearer valido
     *
     * @return
     */
    public String getValidBearer() {
        String token = getValidAccessToken();
        return token == null ? null : "Bearer " + token;
    }

    /**
     * Restituisce il client ID
     *
     * @return
     */
    public String getClientId() {
        return metaData.getString(META_CLIENT_ID_KEY);
    }

    /**
     * Restituisce il client secret
     *
     * @return
     */
    public String getClientSecret() {
        return metaData.getString(META_CLIENT_SECRET_KEY);
    }
}
