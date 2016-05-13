package com.dii.ids.application.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ApiFactory {
    private Context context;
    private SharedPreferences preferences;

    public ApiFactory(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    private void saveIp(String ip) {

    }
}
