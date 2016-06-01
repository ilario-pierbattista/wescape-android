package com.dii.ids.application.navigation.directions;

import com.dii.ids.application.navigation.NavigationIndices;

import java.util.ArrayList;

public class Directions extends ArrayList<Actions> {
    public Actions getCurrent(NavigationIndices indices) {
        return get(indices.current);
    }

    public Actions getNext(NavigationIndices indices) {
        return get(indices.next);
    }
}
