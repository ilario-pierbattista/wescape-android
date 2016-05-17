package com.dii.ids.application.api.response;

import java.util.Calendar;

public class AccessTokenBundle {
    private Calendar calendar;
    private String access_token;
    private String refresh_token;
    private int expires_in;
    private float expiration;

    public AccessTokenBundle() {
        calendar = Calendar.getInstance();
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public float getExpiration() {
        return expiration;
    }

    public void setExpiration(float expiration) {
        this.expiration = expiration;
    }

    public boolean isExpired() {
        float currentTime = calendar.getTimeInMillis();
        return currentTime < expiration;
    }
}
