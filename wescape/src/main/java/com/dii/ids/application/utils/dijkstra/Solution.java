package com.dii.ids.application.utils.dijkstra;

import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    /**
     * Iterate over the Dijkstra solution and divide by floor
     *
     * @param solutionPath A list on nodes representing the solution
     * @return A List with solution divided by floor
     */
    public static List<List<Node>> getSolutionDividedByFloor(List<Node> solutionPath) {
        String floor = "";
        List<List<Node>> solution = new ArrayList<>();
        for (Node node : solutionPath) {
            if (!node.getFloor().equals(floor)) {
                floor = node.getFloor();
                solution.add(new ArrayList<Node>());
            }
            int index = solution.size() == 0 ? 0 : solution.size() - 1;
            solution.get(index).add(node);
        }

        return solution;
    }
}
