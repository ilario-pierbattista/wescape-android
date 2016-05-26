package com.dii.ids.application.entity;

import android.graphics.PointF;

import com.dii.ids.application.entity.db.WescapeDatabase;
import com.dii.ids.application.navigation.Checkpoint;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

@Table(database = WescapeDatabase.class)
public class Node extends BaseModel implements Serializable, Checkpoint {
    public static final String TYPE_GENERAL = "G";
    public static final String TYPE_EXIT = "U";
    public static final String TYPE_ROOM = "R";
    public static final String TYPE_EMERGENCY = "E";

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

    public int getId() {
        return id;
    }    public boolean isEmergencyExit() {
        return type != null && type.equals(TYPE_EMERGENCY);
    }

    public Node setId(int id) {
        this.id = id;
        return this;
    }    public boolean isExit() {
        return type != null && (isEmergencyExit() || type.equals(TYPE_EXIT));
    }

    public String getName() {
        return name;
    }    public boolean isRoom() {
        return type != null && type.equals(TYPE_ROOM);
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }    public boolean isGeneral() {
        return type != null && type.equals(TYPE_GENERAL);
    }

    public double getWidth() {
        return width;
    }    public PointF toPointF() {
        return new PointF(x, y);
    }

    public Node setWidth(double width) {
        this.width = width;
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
    }    public String getFloor() {
        return floor;
    }

    public String getType() {
        return type;
    }    public int getFloorInt() {
        return Integer.parseInt(getFloor());
    }

    public Node setType(String type) {
        this.type = type;
        return this;
    }    public Node setFloor(String floor) {
        this.floor = floor;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return id == node.id;

    }    public Node setFloorInt(int floor) {
        this.floor = String.valueOf(floor);
        return this;
    }

    @Override
    public int hashCode() {
        return id;
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


















}
