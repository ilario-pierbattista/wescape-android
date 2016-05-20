package com.dii.ids.application.entity;

import java.io.Serializable;

public class Position implements Serializable {
    public final double x;
    public final double y;
    public final String floor;

    public Position(double x, double y, String floor) {
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public double distance(int x, int y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
    }

    @Override
    public String toString() {
        return "{x: " + Double.toString(x) + ", y: " +
                Double.toString(y) + ", floor: " +
                floor + "}";
    }
}
