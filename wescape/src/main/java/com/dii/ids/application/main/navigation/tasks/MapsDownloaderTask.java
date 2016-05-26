package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.entity.Map;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.utils.io.SimpleDiskCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class MapsDownloaderTask extends AsyncTask<Integer, Void, Boolean> {
    private static final String LOG_TAG = MapsDownloaderTask.class.getSimpleName();
    private static final String CACHE_SUBDIR = "wescape_maps";
    private static final int CACHE_SIZE = 1024 * 1024 * 10;
    private static SimpleDiskCache imageCache = null;
    private Map map;
    private TaskListener<Map> listener;
    private Context context;
    private Exception thrownException;
    private WescapeService service;

    public MapsDownloaderTask(Context context, TaskListener<Map> listener) {
        this.context = context;
        this.listener = listener;
        this.service = ApiBuilder.buildWescapeService(context);
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        int floor = params[0];

        if (imageCache == null) {
            try {
                imageCache = initCache();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Errore", e);
                thrownException = e;
                return false;
            }
        }

        try {
            Call<ResponseBody> call = service.downloadFloorMap(floor);

            Bitmap image = getBitmapFromMemCache(call.request().url().toString());
            map = new Map(String.valueOf(floor), image);

            if (image == null) {
                Response<ResponseBody> response = call.execute();
                InputStream inputStream = response.body().byteStream();
                image = BitmapFactory.decodeStream(inputStream);
                addBitmapToMemoryCache(call.request().url().toString(), image);
            }
        } catch (IOException e) {
            thrownException = e;
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(map);
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