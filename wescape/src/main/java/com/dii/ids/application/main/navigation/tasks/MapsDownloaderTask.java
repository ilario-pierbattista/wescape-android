package com.dii.ids.application.main.navigation.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dii.ids.application.main.navigation.HomeFragment;
import com.dii.ids.application.providers.EndPointsProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate the user.
 */
public class MapsDownloaderTask extends AsyncTask<Void, Void, Boolean> {
    private HomeFragment fragment;
    private static final String LOG_TAG = MapsDownloaderTask.class.getSimpleName();
    private Bitmap image;

    public MapsDownloaderTask() {
    }

    public MapsDownloaderTask inject(HomeFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        EndPointsProvider endPoints = new EndPointsProvider();
        HttpURLConnection connection;

        // Generazione dell'url
        URL url = endPoints.downloadMaps();
        if (url == null) {
            Log.e(LOG_TAG, "Url malformato");
            return false;
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            image = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            return false;
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(success) {
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
}