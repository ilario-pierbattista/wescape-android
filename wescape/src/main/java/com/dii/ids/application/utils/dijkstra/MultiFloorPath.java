package com.dii.ids.application.utils.dijkstra;

import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class MultiFloorPath extends HashMap<String, Path> {
    private Node origin;
    private Node destination;

    public MultiFloorPath() {
    }

    public Path toPath() {
        return toPath(origin, destination);
    }

    public Path toPath(Node origin, Node destination) {
        int originFloor = origin.getFloorInt();
        int destinationFloor = destination.getFloorInt();
        boolean ascendant = originFloor <= destinationFloor;

        Set<String> floorSet = keySet();
        ArrayList<Integer> floors = new ArrayList<>(floorSet.size());
        for (String floor : floorSet) {
            floors.add(Integer.parseInt(floor));
        }

        if (ascendant) {
            Collections.sort(floors);
        } else {
            Collections.sort(floors, Collections.<Integer>reverseOrder());
        }

        Path path = new Path();
        for (Integer floor : floors) {
            String f = String.valueOf(floor);
            for (Node node : get(f)) {
                path.add(node);
            }
        }

        return path;
    }

    public Node getOrigin() {
        return origin;
    }

    public MultiFloorPath setOrigin(Node origin) {
        this.origin = origin;
        return this;
    }

    public Node getDestination() {
        return destination;
    }

    public MultiFloorPath setDestination(Node destination) {
        this.destination = destination;
        return this;
    }
}
