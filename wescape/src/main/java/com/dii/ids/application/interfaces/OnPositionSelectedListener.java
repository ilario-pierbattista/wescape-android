package com.dii.ids.application.interfaces;

import android.graphics.PointF;

public interface OnPositionSelectedListener {
    void onPositionConfirm(PointF coordinates, int floor, int type);
}
