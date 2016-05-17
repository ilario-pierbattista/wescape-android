package com.dii.ids.application.main.navigation.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.api.ApiBuilder;
import com.dii.ids.application.api.AuthenticationManager;
import com.dii.ids.application.api.service.WescapeService;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.internal.http.OkHeaders;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadDataTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = DownloadDataTask.class.getName();

    private ApiBuilder apiBuilder;
    private AuthenticationManager authenticationManager;
    private TaskListener<List<Node>> listener;
    private List<Node> nodes;

    public DownloadDataTask(ApiBuilder apiBuilder,
                            AuthenticationManager authenticationManager,
                            TaskListener<List<Node>> listener) {
        this.apiBuilder = apiBuilder;
        this.listener = listener;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            WescapeService service = apiBuilder.buildWescapeService();
            Call<List<Node>> call = service.listNodes(
                    authenticationManager.getValidBearer());
            Response<List<Node>> response = call.execute();
            Log.i(TAG, "Response "+response.code());

            switch (response.code()) {
                case HttpURLConnection.HTTP_OK: {
                    nodes = response.body();
                    break;
                }
                case HttpURLConnection.HTTP_FORBIDDEN: {
                    authenticationManager.deleteAccessToken();
                }
            }

            return (nodes != null);
        } catch (IOException e) {
            Log.e(TAG, "API Error", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(nodes);
        } else {
            listener.onTaskError();
        }
        listener.onTaskComplete();
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCancelled();
    }
}
