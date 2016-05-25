package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.auth.SessionManager;
import com.dii.ids.application.api.auth.wescape.WescapeSessionManager;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.listener.TaskListener;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class DownloadEdgesTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = DownloadEdgesTask.class.getName();

    private SessionManager sessionManager;
    private WescapeService service;
    private TaskListener<List<Edge>> listener;
    private List<Edge> edges;
    private Exception thrownException;

    public DownloadEdgesTask(Context context,
                             TaskListener<List<Edge>> listener) {
        this.listener = listener;
        this.sessionManager = new WescapeSessionManager(context);
        this.service = ApiBuilder.buildWescapeService(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Call<List<Edge>> call = service.listEdges(sessionManager.getBearer());
            Response<List<Edge>> response = call.execute();

            switch (response.code()) {
                case HttpURLConnection.HTTP_OK: {
                    edges = response.body();
                    break;
                }
            }

            return (edges != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(edges);
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
