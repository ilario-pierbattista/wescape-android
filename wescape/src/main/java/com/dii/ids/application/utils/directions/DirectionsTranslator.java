package com.dii.ids.application.utils.directions;

import android.content.Intent;
import android.graphics.Point;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.Position;

import java.util.ArrayList;
import java.util.List;

public class DirectionsTranslator {
    private List<Node> nodes;
    private List<Integer> directions;

    public DirectionsTranslator(List<Node> nodes) {
        this.nodes = nodes;
        this.directions = new ArrayList<>();
    }

    public DirectionsTranslator calculateDirections() {
        Node current, previous, next;
        int index = 0;
        previous = null;
        current = nodes.get(index);
        next = nodes.get(index + 1);



        return this;
    }

    private int getDirectionForNextNode(Node prev, Node current, Node next) {

    }

    private double calculateAngle(Node prev, Node current, Node next) {
        Position p1, p2, p3;
        p1 = new Position(prev);
        p2 = new Position(current);
        p3 = new Position(next);

        double d12, d13, d23;
        d12 = p1.distance(p2);
        d13 = p1.distance(p3);
        d23 = p2.distance(p3);

        // angolo, range [0,pi]
        double cosineAngle = Math.acos(
                (Math.pow(d12, 2) + Math.pow(d13, 2) - Math.pow(d23, 2)) /
                        (2 * d12 * d13));
        
    }

    public List<Integer> getDirections() {
        return directions;
    }
}
