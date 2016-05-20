package com.dii.ids.application.entity;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {
    @Test
    public void test() {
        final double x, y, epsilon;
        final String floor;
        final Position position;
        final String toStringResult;

        x = 1.5;
        y = 2.6;
        epsilon = 0.0001; // Double comparison tollerance
        floor = "1";

        position = new Position(x, y, floor);
        Assert.assertEquals(position.x, x, epsilon);
        Assert.assertEquals(position.y, y, epsilon);
        Assert.assertEquals(position.floor, floor);

        toStringResult = position.toString();
        Assert.assertEquals(toStringResult, "{x: 1.5, y: 2.6, floor: 1}");
    }
}