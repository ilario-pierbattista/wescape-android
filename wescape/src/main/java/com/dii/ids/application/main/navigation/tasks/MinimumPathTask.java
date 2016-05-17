package com.dii.ids.application.main.navigation.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;


import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;


public class MinimumPathTask extends AsyncTask<Void, Void, Boolean> {
    public static final String TAG = MinimumPathTask.class.getName();

    @Override
    protected Boolean doInBackground(Void... params) {

        Node node1 = new Node();
        Node node2 = new Node();

        node1.setId(1)
                .setFloor("150")
                .setMeter_x(10)
                .setMeter_y(20)
                .setName("Nodo 1")
                .setX(100)
                .setY(200);

        node2.setId(2)
                .setFloor("150")
                .setMeter_x(30)
                .setMeter_y(20)
                .setName("Nodo 2")
                .setX(300)
                .setY(200);

        Edge edge = new Edge();
        edge.setId(1)
                .setBegin(node1)
                .setEnd(node2)
                .setLength(12.4)
                .setStairs(false)
                .setWidth(2.3)
                .setI(0)
                .setC(0)
                .setLos(0)
                .setV(0);

        HipsterGraph<Node, Edge> graph =
                GraphBuilder.<Node, Edge>create()
                        .connect(node1).to(node2).withEdge(edge)
                        .createUndirectedGraph();

        SearchProblem p = GraphSearchProblem
                .startingFrom(node1)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        Log.i(TAG, Hipster.createDijkstra(p).search(node2).toString());

        return null;
    }
}
