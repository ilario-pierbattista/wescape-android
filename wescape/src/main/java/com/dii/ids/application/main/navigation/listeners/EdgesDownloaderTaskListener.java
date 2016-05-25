package com.dii.ids.application.main.navigation.listeners;


import android.util.Log;

import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.listener.TaskListener;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

public class EdgesDownloaderTaskListener implements TaskListener<List<Edge>> {
    private DatabaseDefinition database;

    public EdgesDownloaderTaskListener() {
        database = FlowManager.getDatabase(WescapeDatabase.class);
    }

    @Override
    public void onTaskSuccess(final List<Edge> edges) {
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Edge edge : edges) {
                    edge.save(databaseWrapper);
                }
            }
        }).build();
        transaction.execute();
    }

    @Override
    public void onTaskError(Exception e) {
        Log.e(TAG, "Download edges fallito", e);
    }

    @Override
    public void onTaskComplete() {
    }

    @Override
    public void onTaskCancelled() {

    }
}
