package com.dii.ids.application.navigation;


import java.util.ArrayList;
import java.util.List;

public class Graph extends ArrayList<Trunk> {
    public static final String TAG = Graph.class.getName();

    public Graph(List<? extends Trunk> trunks) {
        addAll(trunks);
    }

    public Graph() {
    }

    public boolean isConnected() {
        Graph connectedRegion = new Graph();
        Graph unConnectedRegion = new Graph();

        // Spezzo il grafo in una parte connessa ed in una parte non connessa
        connectedRegion.add(get(0));
        for (int i = 1; i < size(); i++) {
            unConnectedRegion.add(get(i));
        }

        int maxIterations = unConnectedRegion.size();
        ArrayList<Trunk> toDeleteTrunks;

        for (int i = 0; i < maxIterations && !unConnectedRegion.isEmpty(); i++) {
            toDeleteTrunks = new ArrayList<>();

            for (Trunk unconnectedTrunk : unConnectedRegion) {
                if(connectedRegion.isConnectedTo(unconnectedTrunk)) {
                    connectedRegion.add(unconnectedTrunk);
                    toDeleteTrunks.add(unconnectedTrunk);
                }
            }

            for (Trunk trunk : toDeleteTrunks) {
                unConnectedRegion.remove(trunk);
            }

        }

        return unConnectedRegion.isEmpty();
    }

    private boolean isConnectedTo(Trunk trunk) {
        for (Trunk graphTrunk : this) {
            if(graphTrunk.isConnectedTo(trunk)) {
                return true;
            }
        }

        return false;
    }

    public Graph createNewGraphWithoutATrunk(Trunk trunkToKill) {
        Graph subGraph = new Graph();
        for (Trunk trunk : this) {
            if (!trunk.equals(trunkToKill)) {
                subGraph.add(trunk);
            }
        }
        return subGraph;
    }

    public Trunk getTrunkToBreakPath(Path path) {
        return searchTrunk(path.get(0), path.get(1));
    }

    public Trunk searchTrunk(Checkpoint checkpoint1, Checkpoint checkpoint2) {
        Trunk found = null;

        for (Trunk trunk : this) {
            if (trunk.isConnecting(checkpoint1, checkpoint2)) {
                found = trunk;
                break;
            }
        }

        return found;
    }
}
