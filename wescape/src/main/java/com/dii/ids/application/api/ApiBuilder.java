package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.main.settings.SettingsActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    // @TODO Passare in produzione
    private static final boolean DEV_ENVIRONMENT = true;

    public static WescapeService buildWescapeService(Context context) {
        return ApiBuilder.buildWescapeService(context, DEV_ENVIRONMENT);
    }

    public static WescapeService buildWescapeService(Context context, boolean development) {
        // @TODO Aggiungere la validazione dell'indirizzo IP

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ipAddress = preferences.getString(SettingsActivity.WESCAPE_HOSTNAME, "10.42.0.1");
        String endpoint = "";
        if (development) {
            endpoint = "app_dev.php/";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + "/" + endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WescapeService.class);
    }
}
