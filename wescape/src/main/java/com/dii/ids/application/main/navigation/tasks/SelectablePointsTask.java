package com.dii.ids.application.main.navigation.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.Position;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;

import java.util.List;

public class SelectablePointsTask extends AsyncTask<Position, Void, Boolean> {
    private static final String TAG = SelectablePointsTask.class.getName();
    private TaskListener<Node> listener;
    private Exception thrownException;
    private Node selectedNode = null;
    private int radius;

    public SelectablePointsTask(TaskListener<Node> listener,
                                int radius) {
        this.listener = listener;
        this.radius = radius;
    }

    @Override
    protected Boolean doInBackground(Position... params) {
        try {
            double minDistance = Double.POSITIVE_INFINITY,
                    distance = 0;
            Position position = params[0];
            List<Node> nodes = NodeRepository.findByFloor(position.floor, (int) position.x, (int) position.y, radius);

            for (Node node : nodes) {
                distance = position.distance(node.getX(), node.getY());
                if (distance < minDistance) {
                    minDistance = distance;
                    selectedNode = node;
                }
            }

            return (selectedNode != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            listener.onTaskSuccess(selectedNode);
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
