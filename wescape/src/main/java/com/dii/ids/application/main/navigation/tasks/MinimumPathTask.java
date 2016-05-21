package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.EdgeRepository;
import com.dii.ids.application.listener.TaskListener;

import java.util.List;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.Function;

public class MinimumPathTask extends AsyncTask<Node, Void, Boolean> {
    public static final String TAG = MinimumPathTask.class.getName();
    private static final double P_V = 0.07;
    private static final double P_I = 0.45;
    private static final double P_LOS = 0.21;
    private static final double P_C = 0.21;
    private static final double P_L = 0.06;

    private MaterialDialog dialog;
    private Context context;
    private Exception thrownException;
    private Algorithm.SearchResult searchResult;
    private TaskListener<Algorithm.SearchResult> listener;
    private boolean emergency;
    private double maxLength;

    public MinimumPathTask(Context context,
                           TaskListener<Algorithm.SearchResult> listener,
                           boolean emergencyStatus) {
        this.context = context;
        this.listener = listener;
        this.emergency = emergencyStatus;
    }

    public MinimumPathTask(Context context,
                           TaskListener<Algorithm.SearchResult> listener) {
        this.context = context;
        this.listener = listener;
        this.emergency = false;
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
    protected Boolean doInBackground(Node... params) {
        try {
            Node beginNode = params[0];
            Node endNode = params[1];

            List<Edge> edges = EdgeRepository.findAll();
            GraphBuilder<Node, Edge> builder = GraphBuilder.create();

            // Costruzione del grafo
            for (Edge edge : edges) {
                builder.connect(edge.getBegin())
                        .to(edge.getEnd())
                        .withEdge(edge);
            }

            // Query del lato più lungo
            final Edge maxLengthEdge = EdgeRepository.findMaxLengthEdge();
            maxLength = maxLengthEdge.getLength();

            HipsterGraph<Node, Edge> graph = builder.createUndirectedGraph();
            SearchProblem problem = GraphSearchProblem
                    .startingFrom(beginNode)
                    .in(graph)
                    .extractCostFromEdges(new Function<Edge, Double>() {
                        @Override
                        public Double apply(Edge edge) {
                            double length = P_L * edge.getLength() / maxLength;
                            double other = (P_I * edge.getI()) +
                                    (P_C * edge.getC()) +
                                    (P_LOS * edge.getLos()) +
                                    (P_V * edge.getV());

                            if(emergency) {
                                return length + other;
                            } else {
                                return length;
                            }
                        }
                    })
                    .build();

            searchResult = Hipster.createDijkstra(problem).search(endNode);
            return (searchResult != null);
        } catch (Exception e) {
            thrownException = e;
            return false;
        }
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
