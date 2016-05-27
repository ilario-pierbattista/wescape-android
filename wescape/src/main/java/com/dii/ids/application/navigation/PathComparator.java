package com.dii.ids.application.navigation;

import java.util.Comparator;

import es.usc.citius.hipster.model.impl.WeightedNode;


public class PathComparator implements Comparator<Path> {
    @Override
    public int compare(Path lhs, Path rhs) {
        return lhs.getGoalState().compareTo(rhs.getGoalState());
    }
}