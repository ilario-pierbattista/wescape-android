package com.dii.ids.application.providers;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Questa classe mette a disposizione i metodi per la generazione degli URL corrispondenti agli
 * endpoint del server RESTful associato all'applicazione.
 *
 * @TODO Se priva di stato, la classe potrebbe essere convertita a statica
 */
public class EndPointsProvider {
    public static final String SCHEME = "http";
    public static final String AUTHORITY = "wescape.altervista.org";
    private static final String LOG_TAG = EndPointsProvider.class.getSimpleName();
    public Uri.Builder builder;

    public EndPointsProvider() {
        builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY);
    }

    public URL downloadMap(int floor) {
        builder.appendPath("maps")
                .appendPath(Integer.toString(floor).concat(".jpg"));
        try {
            return new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Url Error:", e);
            return null;
        }
    }
}
