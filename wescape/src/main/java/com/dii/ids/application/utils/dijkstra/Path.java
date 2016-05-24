package com.dii.ids.application.utils.dijkstra;

import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class Path extends ArrayList<Node> {
    public Path() {
    }

    public Path(List<Node> nodes) {
        addAll(nodes);
    }

    /**
     * Iterate over a path and divide it by floor
     *
     * @return A List with solution divided by floor
     */
    public MultiFloorPath toMultiFloorPath() {
        String floor = null;
        MultiFloorPath multiFloorPath = new MultiFloorPath();

        multiFloorPath.setOrigin(get(0));
        multiFloorPath.setDestination(get(size() - 1));

        for (Node node : this) {
            if (!node.getFloor().equals(floor)) {
                floor = node.getFloor();
                multiFloorPath.put(floor, new Path());
            }
            multiFloorPath.get(node.getFloor()).add(node);
        }

        return multiFloorPath;
    }
}
