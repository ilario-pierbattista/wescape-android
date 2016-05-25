package com.dii.ids.application.main.navigation.listeners;


import android.util.Log;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.navigation.tasks.EdgesDownloaderTask;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * Responsible for downloading nodes
 */
public class NodesDownloaderTaskListener implements TaskListener<List<Node>> {
    private DatabaseDefinition database;

    public NodesDownloaderTaskListener() {
        database = FlowManager.getDatabase(WescapeDatabase.class);
    }

    @Override
    public void onTaskSuccess(final List<Node> nodes) {
        List<Node> savedNodes = NodeRepository.findAll();
        if (savedNodes.size() != nodes.size()) {
            NodeRepository.deleteAll();
        }

        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Node node : nodes) {
                    node.save(databaseWrapper);
                }
            }
        }).build();

        transaction.execute();
    }

    @Override
    public void onTaskError(Exception e) {
        Log.e(TAG, "Download nodes fallito", e);
    }

    @Override
    public void onTaskComplete() {
    }

    @Override
    public void onTaskCancelled() {

    }
}