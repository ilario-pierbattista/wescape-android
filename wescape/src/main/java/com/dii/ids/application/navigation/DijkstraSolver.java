package com.dii.ids.application.navigation;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.Function;

public class DijkstraSolver {
    public static final String TAG = DijkstraSolver.class.getName();
    private Checkpoint origin;
    private Graph graph;
    private SearchProblem problem;
    private double normalizationBasis;
    private boolean emergency = false;

    /**
     * Imposta il nodo di partenza
     *
     * @param origin
     * @return
     */
    public DijkstraSolver startingFrom(Checkpoint origin) {
        this.origin = origin;
        return this;
    }

    /**
     * Imposta il grafo
     *
     * @param graph
     * @return
     */
    public DijkstraSolver in(Graph graph) {
        this.graph = graph;
        return this;
    }

    /**
     * Cerca le due soluzioni per raggiungere la destinazione
     *
     * @param destination
     * @return
     */
    public List<Path> search(Checkpoint destination) {
        List<Path> solutions = new ArrayList<>();

        // Ricerca della prima soluzione
        buildProblemWithGraph(graph);
        solutions.add(getPathToReachDestination(destination));

        // Ricerca della seconda soluzione
        Graph subGraph = graph.removePath(solutions.get(0));
        if(subGraph.isConnected()) {
            buildProblemWithGraph(subGraph);
            solutions.add(getPathToReachDestination(destination));
        }

        return solutions;
    }

    public List<Path> searchNearestExits(List<Checkpoint> exits) {
        List<Path> solutions = new ArrayList<>();
        Algorithm.SearchResult result;

        buildProblemWithGraph(graph);
        for (Checkpoint exit : exits) {
            result = Hipster.createDijkstra(problem).search(exit);
            Path path = getFirstPath(result);
            path.setGoalState((WeightedNode) result.getGoalNode());
            solutions.add(path);
        }

        // @TODO remove
        Log.i(TAG, solutions.toString());
        Collections.sort(solutions, new PathComparator());
        Log.i(TAG, solutions.toString());

        return new ArrayList<>(solutions.subList(0, 2));
    }

    private Path getPathToReachDestination(Checkpoint destination) {
        Algorithm.SearchResult result =
                Hipster.createDijkstra(problem).search(destination);

        WeightedNode goalState = (WeightedNode) result.getGoalNode();
        Log.i(TAG, goalState.getScore().toString());

        return getFirstPath(result);
    }

    private DijkstraSolver buildProblemWithGraph(Graph graphProblem) {
        // Creazione del grafo
        GraphBuilder<Checkpoint, Trunk> graphBuilder = GraphBuilder.create();
        for (Trunk trunk : graphProblem) {
            graphBuilder.connect(trunk.getBegin())
                    .to(trunk.getEnd())
                    .withEdge(trunk);
        }
        HipsterGraph<Checkpoint, Trunk> hipsterGraph = graphBuilder
                .createUndirectedGraph();

        // Creazione del problema
        problem = GraphSearchProblem
                .startingFrom(origin)
                .in(hipsterGraph)
                .extractCostFromEdges(new Function<Trunk, Double>() {
                    @Override
                    public Double apply(Trunk edge) {
                        return edge.getCost(normalizationBasis, emergency);
                    }
                })
                .build();

        return this;
    }

    private Path getFirstPath(Algorithm.SearchResult result) {
        return new Path((List<Checkpoint>) result.getOptimalPaths().get(0));
    }

    public DijkstraSolver setNormalizationBasis(double normalizationBasis) {
        this.normalizationBasis = normalizationBasis;
        return this;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public DijkstraSolver setEmergency(boolean emergency) {
        this.emergency = emergency;
        return this;
    }
}
