package com.dii.ids.application.main.navigation.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.main.navigation.MapFragment;
import com.dii.ids.application.providers.EndPointsProvider;
import com.dii.ids.application.utils.io.SimpleDiskCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class MapsDownloaderTask extends AsyncTask<Integer, Void, Boolean> {
    private MapFragment fragment;
    private static final String LOG_TAG = MapsDownloaderTask.class.getSimpleName();
    private static final String CACHE_SUBDIR = "wescape_maps";
    private static final int CACHE_SIZE = 1024 * 1024 * 10;
    private Bitmap image;
    private static SimpleDiskCache imageCache = null;

    public MapsDownloaderTask() {
    }

    public MapsDownloaderTask inject(MapFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        EndPointsProvider endPoints = new EndPointsProvider();
        HttpURLConnection connection;
        int floor = params[0];

        if(imageCache == null) {
            try {
                imageCache = initCache();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Errore", e);
                return false;
            }
        }

        // Generazione dell'url
        URL url = endPoints.downloadMap(floor);
        if (url == null) {
            Log.e(LOG_TAG, "Url malformato");
            return false;
        }

        try {
            image = getBitmapFromMemCache(url.toString());
            if (image == null) {
                connection = (HttpURLConnection) url.openConnection();
                image = BitmapFactory.decodeStream(connection.getInputStream());
                addBitmapToMemoryCache(url.toString(), image);
            }
        } catch (IOException e) {
            return false;
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            fragment.onTaskSuccess(this);
        } else {
            fragment.onTaskError(this);
        }
    }

    @Override
    protected void onCancelled() {
        fragment.onTaskCancelled(this);
    }

    public Bitmap getImage() {
        return image;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            try {
                OutputStream outputStream = imageCache.openStream(key);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        try {
            SimpleDiskCache.BitmapEntry entry = imageCache.getBitmap(key);
            if(entry != null) {
                return entry.getBitmap();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private SimpleDiskCache initCache() throws IOException {
        File cacheDir = new File(fragment.getContext().getCacheDir(), CACHE_SUBDIR);
        return SimpleDiskCache.open(cacheDir, 0, CACHE_SIZE);
    }
}