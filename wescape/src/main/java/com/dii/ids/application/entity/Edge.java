package com.dii.ids.application.entity;

import com.dii.ids.application.db.WescapeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = WescapeDatabase.class)
public class Edge extends BaseModel {

    @PrimaryKey
    private int id;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private Node begin;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private Node end;

    @Column
    private double length;

    @Column
    private double width;

    @Column
    private boolean stairs;

    @Column
    private double v;

    @Column
    private double i;

    @Column
    private double los;

    @Column
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

    public boolean getStairs() {
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

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", begin=" + begin +
                ", end=" + end +
                ", length=" + length +
                ", width=" + width +
                ", stairs=" + stairs +
                ", v=" + v +
                ", i=" + i +
                ", los=" + los +
                ", c=" + c +
                '}';
    }
}
