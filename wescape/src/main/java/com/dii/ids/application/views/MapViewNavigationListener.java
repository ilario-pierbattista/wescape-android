package com.dii.ids.application.views;

import com.dii.ids.application.navigation.Checkpoint;

public interface MapViewNavigationListener {
    void onNext();
    void onPrevious();
    void saveVisitedNode(Checkpoint visited);
}
