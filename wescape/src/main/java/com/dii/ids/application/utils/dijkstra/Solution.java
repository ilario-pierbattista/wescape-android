package com.dii.ids.application.utils.dijkstra;

import android.util.Log;

import com.dii.ids.application.entity.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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


    public static List<Node> getOrderedSolution(Node origin, Node destination, HashMap<String, List<Node>> multiFloorPath) {
        int originFloor = origin.getFloorInt();
        int destinationFloor = destination.getFloorInt();
        boolean ascendant = originFloor <= destinationFloor;

        Set<String> floorSet = multiFloorPath.keySet();
        ArrayList<Integer> floors = new ArrayList<>(floorSet.size());
        for (String floor : floorSet) {
            floors.add(Integer.parseInt(floor));
        }

        if (ascendant) {
            Collections.sort(floors);
        } else {
            Collections.sort(floors, Collections.<Integer>reverseOrder());
        }

        List<Node> orderedSolution = new ArrayList<>();
        for (Integer floor : floors) {
            String f = String.valueOf(floor);
            for (Node node : multiFloorPath.get(f)) {
                orderedSolution.add(node);
            }
        }

        return orderedSolution;
    }
}
