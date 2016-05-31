package com.dii.ids.application.main.navigation.tasks;


import android.os.AsyncTask;

import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.navigation.DijkstraSolver;
import com.dii.ids.application.navigation.Graph;
import com.dii.ids.application.navigation.Path;

public class ContinuousMPSTask extends AsyncTask<Node, Void, Boolean> {
    private TaskListener<Path> listener;
    private Exception thrownException;
    private Path minimumPath;
    private Edge excludedEdge;
    private boolean emergency;

    public ContinuousMPSTask(TaskListener<Path> listener,
                             Edge excludedEdge,
                             boolean emergency) {
        this.listener = listener;
        this.emergency = emergency;
        this.excludedEdge = excludedEdge;
    }

    @Override
    protected Boolean doInBackground(Node... params) {
        try {
            Node origin = params[0];
            Node destination = params[1];

            Graph graph = new Graph(EdgeRepository.findAllButOne(excludedEdge));
            DijkstraSolver solver = new DijkstraSolver();
            solver.startingFrom(origin)
                    .in(graph)
                    .setNormalizationBasis(EdgeRepository.getMaxLength())
                    .setEmergency(emergency);
            minimumPath = solver.search(destination);

            return minimumPath != null;
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onTaskSuccess(minimumPath);
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
