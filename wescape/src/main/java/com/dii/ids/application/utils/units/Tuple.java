package com.dii.ids.application.utils.units;

/**
 * Wrapper di una tupla
 *
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
