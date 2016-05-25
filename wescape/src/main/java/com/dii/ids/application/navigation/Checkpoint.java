package com.dii.ids.application.navigation;


import android.graphics.PointF;

public interface Checkpoint {
    String getFloor();
    int getFloorInt();
    boolean isGeneral();
    boolean isRoom();
    boolean isExit();
    boolean isEmergencyExit();
    PointF toPointF();
}
