package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dii.ids.application.api.service.OAuth2Service;
import com.dii.ids.application.main.settings.SettingsActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    private Context context;
    private SharedPreferences preferences;

    public ApiBuilder(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public OAuth2Service buildAuthService() {
        // @TODO Aggiungere la validazione dell'indirizzo IP
        String ipAddress = this.preferences.getString(SettingsActivity.WESCAPE_HOSTNAME, "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(OAuth2Service.class);
    }
}
