package com.dii.ids.application.providers;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class EndPointsProvider {
    public static final String SCHEME = "http";
    public static final String AUTHORITY = "wescape.altervista.org";
    public Uri.Builder builder;
    private static final String LOG_TAG = EndPointsProvider.class.getSimpleName();

    public EndPointsProvider() {
        builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY);
    }

    public URL downloadMapsZip() {
        builder.appendPath("maps")
                .appendPath("get_images.php");
        try {
            return new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Url Error:", e);
            return null;
        }
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
