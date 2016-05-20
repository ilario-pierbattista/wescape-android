package com.dii.ids.application.main.navigation.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;


import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;


public class MinimumPathTask extends AsyncTask<Node, Void, Boolean> {
    public static final String TAG = MinimumPathTask.class.getName();
    private MaterialDialog dialog;
    private Context context;

    public MinimumPathTask(Context context) {
        this.context = context;
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
        Node startNode = params[0];
        Node endingNode = params[1];

        // Simulazione del calcolo
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Edge edge = new Edge();
//        edge.setId(1)
//                .setBegin(startNode)
//                .setEnd(endingNode)
//                .setLength(12.4)
//                .setStairs(false)
//                .setWidth(2.3)
//                .setI(0)
//                .setC(0)
//                .setLos(0)
//                .setV(0);
//
//        HipsterGraph<Node, Edge> graph =
//                GraphBuilder.<Node, Edge>create()
//                        .connect(node1).to(node2).withEdge(edge)
//                        .createUndirectedGraph();
//
//        SearchProblem p = GraphSearchProblem
//                .startingFrom(node1)
//                .in(graph)
//                .takeCostsFromEdges()
//                .build();
//
//        Log.i(TAG, Hipster.createDijkstra(p).search(node2).toString());

        return null;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
