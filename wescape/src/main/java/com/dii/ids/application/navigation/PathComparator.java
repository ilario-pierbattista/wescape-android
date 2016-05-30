package com.dii.ids.application.navigation;

import java.util.Comparator;


public class PathComparator implements Comparator<Path> {
    @Override
    public int compare(Path lhs, Path rhs) {
        return lhs.getGoalState().compareTo(rhs.getGoalState());
    }
}