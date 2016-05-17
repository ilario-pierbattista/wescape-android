package com.dii.ids.application.entity;

public class Node {
    private int id;
    private String name;
    private String floor;
    private double width;
    private int x;
    private int y;
    private int meter_x;
    private int meter_y;

    public int getId() {
        return id;
    }

    public Node setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public String getFloor() {
        return floor;
    }

    public Node setFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public double getWidth() {
        return width;
    }

    public Node setWidth(double width) {
        this.width = width;
        return this;
    }

    public int getX() {
        return x;
    }

    public Node setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Node setY(int y) {
        this.y = y;
        return this;
    }

    public int getMeter_x() {
        return meter_x;
    }

    public Node setMeter_x(int meter_x) {
        this.meter_x = meter_x;
        return this;
    }

    public int getMeter_y() {
        return meter_y;
    }

    public Node setMeter_y(int meter_y) {
        this.meter_y = meter_y;
        return this;
    }
}
