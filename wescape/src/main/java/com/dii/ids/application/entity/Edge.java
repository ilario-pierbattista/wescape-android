package com.dii.ids.application.entity;

import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.navigation.Checkpoint;
import com.dii.ids.application.navigation.Trunk;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = WescapeDatabase.class)
public class Edge extends BaseModel implements Trunk {
    private static final double P_V = 0.07;
    private static final double P_I = 0.45;
    private static final double P_LOS = 0.21;
    private static final double P_C = 0.21;
    private static final double P_L = 0.06;

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

    @Override
    public double getCost(double normalizationBasis, boolean emergency) {
        double length = P_L * getLength() / normalizationBasis;
        double other = (P_I * i) +
                (P_C * c) +
                (P_LOS * los) +
                (P_V * v);

        if (emergency) {
            return length + other;
        } else {
            return length;
        }
    }

    @Override
    public boolean isConnecting(Checkpoint checkpoint1, Checkpoint checkpoint2) {
        boolean condition1, condition2;

        condition1 = begin.equals(checkpoint1) && end.equals(checkpoint2);
        condition2 = begin.equals(checkpoint2) && end.equals(checkpoint1);

        return (condition1 && !condition2) || (!condition1 && condition2);
    }

    @Override
    public boolean isConnectedTo(Checkpoint checkpoint) {
        return begin.equals(checkpoint) || end.equals(checkpoint);
    }

    @Override
    public boolean isConnectedTo(Trunk trunk) {
        return begin.equals(trunk.getBegin()) ||
                begin.equals(trunk.getEnd()) ||
                end.equals(trunk.getBegin()) ||
                end.equals(trunk.getEnd());
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return id == edge.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
