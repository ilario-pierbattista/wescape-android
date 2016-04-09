package com.dii.ids.application.entities;

import java.io.Serializable;

public class Position implements Serializable {
    public final double x;
    public final double y;
    public final int floor;

    public Position(double x, double y, int floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "{x: " + Double.toString(x) + ", y: " +
                Double.toString(y) + ", floor: " +
                Integer.toString(floor) + "}";
    }
}
