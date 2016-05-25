package com.dii.ids.application.navigation;

import java.util.ArrayList;
import java.util.List;

public class Path extends ArrayList<Checkpoint> {
    public Path() {
    }

    public Path(List<Checkpoint> nodes) {
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

        for (Checkpoint checkpoint : this) {
            if (!checkpoint.getFloor().equals(floor)) {
                floor = checkpoint.getFloor();
                multiFloorPath.put(floor, new Path());
            }
            multiFloorPath.get(checkpoint.getFloor()).add(checkpoint);
        }

        return multiFloorPath;
    }
}
