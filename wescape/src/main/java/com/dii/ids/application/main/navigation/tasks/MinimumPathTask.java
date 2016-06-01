package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.navigation.DijkstraSolver;
import com.dii.ids.application.navigation.Graph;
import com.dii.ids.application.navigation.Path;

import java.util.List;

public class MinimumPathTask extends AsyncTask<Node, Void, Boolean> {
    public static final String TAG = MinimumPathTask.class.getName();

    private MaterialDialog dialog;
    private Context context;
    private Exception thrownException;
    private List<Path> searchResult;
    private TaskListener<List<Path>> listener;

    public MinimumPathTask(Context context,
                           TaskListener<List<Path>> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Node... params) {
        try {
            Node beginNode = params[0];
            Node endNode = params[1];
            Graph graph = new Graph(EdgeRepository.findAll());
            DijkstraSolver dijkstraSolver = new DijkstraSolver();

            dijkstraSolver.startingFrom(beginNode)
                    .in(graph)
                    .setNormalizationBasis(EdgeRepository.getMaxLength());
            searchResult = dijkstraSolver.searchDoublePath(endNode);

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
                .title(context.getString(R.string.computing_route))
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
