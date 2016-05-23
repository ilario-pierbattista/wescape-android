package com.dii.ids.application.entity;

import android.graphics.Point;
import android.graphics.PointF;

import com.dii.ids.application.db.WescapeDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

@Table(database = WescapeDatabase.class)
public class Node extends BaseModel implements Serializable{

    @PrimaryKey
    private int id;

    @Column
    private String name;

    @Column
    private String floor;

    @Column
    private double width;

    @Column
    private int x;

    @Column
    private int y;

    @Column
    private int meter_x;

    @Column
    private int meter_y;

    @Column
    private String type;

    public PointF toPointF() {
        return new PointF(x, y);
    }

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

    public String getType() {
        return type;
    }

    public Node setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", floor='" + floor + '\'' +
                ", width=" + width +
                ", x=" + x +
                ", y=" + y +
                ", meter_x=" + meter_x +
                ", meter_y=" + meter_y +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return id == node.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
