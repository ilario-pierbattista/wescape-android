package com.dii.ids.application.entity;


public class Edge {
    private int id;
    private Node begin;
    private Node end;
    private double length;
    private double width;
    private boolean stairs;
    private double v;
    private double i;
    private double los;
    private double c;

    public int getId() {
        return id;
    }

    public Edge setId(int id) {
        this.id = id;
        return this;
    }

    public Node getBegin() {
        return begin;
    }

    public Edge setBegin(Node begin) {
        this.begin = begin;
        return this;
    }

    public Node getEnd() {
        return end;
    }

    public Edge setEnd(Node end) {
        this.end = end;
        return this;
    }

    public double getLength() {
        return length;
    }

    public Edge setLength(double length) {
        this.length = length;
        return this;
    }

    public double getWidth() {
        return width;
    }

    public Edge setWidth(double width) {
        this.width = width;
        return this;
    }

    public boolean isStairs() {
        return stairs;
    }

    public Edge setStairs(boolean stairs) {
        this.stairs = stairs;
        return this;
    }

    public double getV() {
        return v;
    }

    public Edge setV(double v) {
        this.v = v;
        return this;
    }

    public double getI() {
        return i;
    }

    public Edge setI(double i) {
        this.i = i;
        return this;
    }

    public double getLos() {
        return los;
    }

    public Edge setLos(double los) {
        this.los = los;
        return this;
    }

    public double getC() {
        return c;
    }

    public Edge setC(double c) {
        this.c = c;
        return this;
    }
}
