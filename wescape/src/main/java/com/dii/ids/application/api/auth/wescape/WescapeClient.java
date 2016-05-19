package com.dii.ids.application.api.auth.wescape;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.dii.ids.application.api.auth.Client;

public class WescapeClient implements Client {
    public static final String TAG = WescapeClient.class.getName();
    public static final String META_CLIENT_ID_KEY = "com.dii.ids.application.WESCAPE_CLIENT_ID";
    public static final String META_CLIENT_SECRET_KEY = "com.dii.ids.application.WESCAPE_CLIENT_SECRET";

    private Bundle metaData;

    public WescapeClient(Context context) {
        try {
            metaData = (context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA
            )).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Errore nel caricare i metadati", e);
        }
    }

    /**
     * Restituisce il client ID
     *
     * @return
     */
    public String getId() {
        return metaData.getString(META_CLIENT_ID_KEY);
    }

    /**
     * Restituisce il client secret
     *
     * @return
     */
    public String getSecret() {
        return metaData.getString(META_CLIENT_SECRET_KEY);
    }
}
