package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.navigation.DijkstraSolver;
import com.dii.ids.application.navigation.Graph;
import com.dii.ids.application.navigation.Path;

import java.util.List;

public class NearestExitTask extends AsyncTask<Node, Void, Boolean> {

    public static final String TAG = NearestExitTask.class.getName();
    private MaterialDialog dialog;
    private Context context;
    private Exception thrownException;
    private TaskListener<List<Path>> listener;
    private List<Path> searchResult;

    public NearestExitTask(Context context, TaskListener<List<Path>> listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Node... params) {
        try {
            Node origin = params[0];
            Graph graph = new Graph(EdgeRepository.findAll());
            List<Node> exits = NodeRepository.findAllExits();
            final Edge maxLengthEdge = EdgeRepository.findMaxLengthEdge();
            DijkstraSolver solver = new DijkstraSolver();

            solver.setNormalizationBasis(maxLengthEdge.getLength())
                    .setEmergency(true)
                    .startingFrom(origin)
                    .in(graph);

            searchResult = solver.searchNearestExits(exits);

            Thread.sleep(1000);
            return (searchResult != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.find_nearest_exit))
                .content(context.getString(R.string.please_wait))
                .progress(true, 0)
                .widgetColorRes(R.color.regularBlue)
                .show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success) {
            listener.onTaskSuccess(searchResult);
        } else {
            listener.onTaskError(thrownException);
        }
        listener.onTaskComplete();
    }
}
