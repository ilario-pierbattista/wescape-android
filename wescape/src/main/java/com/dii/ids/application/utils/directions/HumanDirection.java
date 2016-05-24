package com.dii.ids.application.utils.directions;

import java.io.Serializable;

public class HumanDirection implements Serializable {
    private String direction;
    private int iconResource;

    public HumanDirection(String direction, int iconResource) {
        this.direction = direction;
        this.iconResource = iconResource;
    }

    public HumanDirection() {}

    public String getDirection() {

        return direction;
    }

    public HumanDirection setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public int getIconResource() {
        return iconResource;
    }

    public HumanDirection setIconResource(int iconResource) {
        this.iconResource = iconResource;
        return this;
    }

    @Override
    public String toString() {
        return "HumanDirection{" +
                "direction='" + direction + '\'' +
                ", iconResource=" + iconResource +
                '}';
    }
}
