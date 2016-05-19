package com.dii.ids.application.api.auth.wescape;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Gestisce la persistenza del token
 */
public class TokenStorage {
    public static final String TAG = TokenStorage.class.getName();
    public static final String ACCESS_TOKEN_KEY = TokenStorage.class.getCanonicalName() + ".AccessToken";
    public static final String REFRESH_TOKEN_KEY = TokenStorage.class.getCanonicalName() + ".RefreshToken";
    public static final String ACCESS_EXPIRATION_DATE = TokenStorage.class.getCanonicalName() + ".AccessTokenExpirationDate";
    public static final String ACCESS_EXPIRES_IN = TokenStorage.class.getCanonicalName() + ".AccessTokenExpiresIn";

    private Token currentToken;
    private SharedPreferences preferences;

    /**
     * Costruttore
     *
     * @param context
     */
    public TokenStorage(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Salva il token
     *
     * @param token
     */
    public void save(Token token) {
        currentToken = token;
        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, token.getAccessToken())
                .putString(REFRESH_TOKEN_KEY, token.getRefreshToken())
                .putFloat(ACCESS_EXPIRATION_DATE, token.getExpiration())
                .putInt(ACCESS_EXPIRES_IN, token.getExpiresIn())
                .apply();
    }

    /**
     * Restituisce il token salvato
     *
     * @return
     */
    public Token get() {
        if (currentToken != null) {
            return currentToken;
        }

        Token accessToken = new Token();
        accessToken.setAccessToken(preferences.getString(ACCESS_TOKEN_KEY, null));
        accessToken.setRefreshToken(preferences.getString(REFRESH_TOKEN_KEY, null));
        accessToken.setExpiresIn(preferences.getInt(ACCESS_EXPIRES_IN, 0));
        accessToken.setExpiration(preferences.getFloat(ACCESS_EXPIRATION_DATE, 0));

        if (accessToken.getAccessToken() != null) {
            currentToken = accessToken;
        }
        return currentToken;
    }

    /**
     * Cancella il token salvato
     */
    public void delete() {
        currentToken = null;
        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, null)
                .putString(REFRESH_TOKEN_KEY, null)
                .putString(ACCESS_EXPIRATION_DATE, null)
                .putString(ACCESS_EXPIRES_IN, null)
                .apply();
    }
}
