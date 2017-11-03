package com.dii.ids.application.main.authentication.tasks;

import android.os.AsyncTask;

import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.listener.TaskListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * It loads the offline assets for completely server-free usage.
 */
public class LoadAssetsTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = LoadAssetsTask.class.getName();
    private TaskListener<Void> listener;
    private Exception thrownException;
    private Gson gson;
    private InputStream nodeStream, edgeStream;
    private static final Type NODE_TYPE = new TypeToken<List<Node>>() {
    }.getType();
    private static final Type EDGE_TYPE = new TypeToken<List<Edge>>() {
    }.getType();
    private DatabaseDefinition database;

    public LoadAssetsTask(TaskListener<Void> listener, InputStream nodeStream, InputStream edgeStream) {
        this.listener = listener;
        this.nodeStream = nodeStream;
        this.edgeStream = edgeStream;
        database = FlowManager.getDatabase(WescapeDatabase.class);
        gson = new Gson();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            JsonReader nodeReader = new JsonReader(new InputStreamReader(nodeStream));
            JsonReader edgeReader = new JsonReader(new InputStreamReader(edgeStream));
            final List<Node> nodes = gson.fromJson(nodeReader, NODE_TYPE);
            final List<Edge> edges = gson.fromJson(edgeReader, EDGE_TYPE);

            Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Node node : nodes) {
                        node.save(databaseWrapper);
                    }
                    for (Edge edge : edges) {
                        edge.save(databaseWrapper);
                    }
                }
            }).build();
            transaction.execute();

            return true;
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(null);
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