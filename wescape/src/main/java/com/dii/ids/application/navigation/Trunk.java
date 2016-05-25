package com.dii.ids.application.navigation;


public interface Trunk {
    Checkpoint getBegin();
    Checkpoint getEnd();
    double getCost(double normalizationBasis, boolean emergency);
    boolean isConnecting(Checkpoint checkpoint1, Checkpoint checkpoint2);
    boolean isConnectedTo(Trunk trunk);
}
