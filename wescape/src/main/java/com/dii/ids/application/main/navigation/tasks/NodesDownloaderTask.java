package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.auth.wescape.WescapeSessionManager;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.settings.SettingsActivity;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NodesDownloaderTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = NodesDownloaderTask.class.getName();

    private SessionManager sessionManager;
    private WescapeService service;
    private TaskListener<List<Node>> listener;
    private List<Node> nodes;
    private Exception thrownException;

    public NodesDownloaderTask(Context context,
                               TaskListener<List<Node>> listener) {

        String ipAddress = (PreferenceManager.getDefaultSharedPreferences(context))
                .getString(SettingsActivity.WESCAPE_HOSTNAME,
                        SettingsActivity.WESCAPE_DEFAULT_HOSTNAME);
        this.listener = listener;
        this.sessionManager = new WescapeSessionManager(context, ipAddress);
        this.service = ApiBuilder.buildWescapeService(ipAddress);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Call<List<Node>> call = service.listNodes(sessionManager.getBearer());
            Response<List<Node>> response = call.execute();
            Log.i(TAG, "Response " + response.code());

            switch (response.code()) {
                case HttpURLConnection.HTTP_OK: {
                    nodes = response.body();
                    break;
                }
            }

            return (nodes != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(nodes);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }
}
