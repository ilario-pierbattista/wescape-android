package com.dii.ids.application.utils.dijkstra;

import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution {

    /**
     * Iterate over the Dijkstra solution and divide by floor
     *
     * @param solutionPath A list on nodes representing the solution
     * @return A List with solution divided by floor
     */
    public static HashMap<String, List<Node>> getSolutionDividedByFloor(List<Node> solutionPath) {
        String floor = "";
        HashMap<String, List<Node>> solution = new HashMap<>();
        for (Node node : solutionPath) {
            if (!node.getFloor().equals(floor)) {
                floor = node.getFloor();
                solution.put(floor, new ArrayList<Node>());
            }
            //String index = solution.size() == 0 ? String.valueOf(0) : String.valueOf(solution.size() - 1);
            solution.get(node.getFloor()).add(node);
        }

        return solution;
    }
}
