package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.main.settings.SettingsActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    public static WescapeService buildWescapeService(Context context) {
        // @TODO Aggiungere la validazione dell'indirizzo IP

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ipAddress = preferences.getString(SettingsActivity.WESCAPE_HOSTNAME, "10.42.0.1");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WescapeService.class);
    }
}
