package com.dii.ids.application.api.auth.wescape;

import com.dii.ids.application.api.response.TokenResponse;

import java.util.Calendar;

public class Token {
    // Si va scadere il token 100 secondi prima del dovuto per evitare errori di sincronizzazione
    private static final int EXPIRATION_TOLLERANCE = 100;

    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private float expiration;

    public Token() {
    }

    public Token(TokenResponse tokenResponse) {
        Calendar calendar = Calendar.getInstance();
        float currentTime = calendar.getTimeInMillis();

        accessToken = tokenResponse.getAccess_token();
        refreshToken = tokenResponse.getRefresh_token();
        expiresIn = tokenResponse.getExpires_in();

        expiration = currentTime + (expiresIn * 1000) - EXPIRATION_TOLLERANCE;
    }

    public boolean isExpired() {
        Calendar calendar = Calendar.getInstance();
        float currentTime = calendar.getTimeInMillis();

        return currentTime >= expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Token setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Token setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public Token setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public float getExpiration() {
        return expiration;
    }

    public Token setExpiration(float expiration) {
        this.expiration = expiration;
        return this;
    }
}
