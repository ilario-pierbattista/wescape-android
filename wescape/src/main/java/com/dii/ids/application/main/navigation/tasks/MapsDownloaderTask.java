package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.api.EndPointsProvider;
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
    private static final String LOG_TAG = MapsDownloaderTask.class.getSimpleName();
    private static final String CACHE_SUBDIR = "wescape_maps";
    private static final int CACHE_SIZE = 1024 * 1024 * 10;
    private static SimpleDiskCache imageCache = null;
    private Bitmap image;
    private TaskListener<Bitmap> listener;
    private Context context;
    private Exception thrownException;

    public MapsDownloaderTask(Context context, TaskListener<Bitmap> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        EndPointsProvider endPoints = new EndPointsProvider();
        HttpURLConnection connection;
        int floor = params[0];

        if (imageCache == null) {
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
            thrownException = e;
            return false;
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(image);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }

    private SimpleDiskCache initCache() throws IOException {
        File cacheDir = new File(context.getCacheDir(), CACHE_SUBDIR);
        return SimpleDiskCache.open(cacheDir, 0, CACHE_SIZE);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        try {
            SimpleDiskCache.BitmapEntry entry = imageCache.getBitmap(key);
            if (entry != null) {
                return entry.getBitmap();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
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
}