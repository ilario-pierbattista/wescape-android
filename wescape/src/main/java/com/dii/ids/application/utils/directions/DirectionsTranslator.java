package com.dii.ids.application.utils.directions;

import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dii.ids.application.entity.Node;
import com.dii.ids.application.utils.algebra.TridimensionalVector;

import java.util.ArrayList;
import java.util.List;

public class DirectionsTranslator {
    private static final double STRAIGHT_TRUNK_TOLLERANCE_ANGLE = 45.0;
    private static final double CURVED_BACK_TRUNK_MIN_ANGLE = 130.0;

    private List<Node> nodes;
    private List<Integer> directions;
    private final double straightAngleLowerBound,
            straightAngleUpperBound,
            curvedAngleLowerUpperBound,
            curvedAngleUpperLowerBound;

    public DirectionsTranslator(List<Node> nodes) {
        this.nodes = nodes;
        this.directions = new ArrayList<>();

        straightAngleLowerBound = Math.PI - Math.toRadians(STRAIGHT_TRUNK_TOLLERANCE_ANGLE);
        straightAngleUpperBound = Math.PI + Math.toRadians(STRAIGHT_TRUNK_TOLLERANCE_ANGLE);
        curvedAngleLowerUpperBound = Math.PI - Math.toRadians(CURVED_BACK_TRUNK_MIN_ANGLE);
        curvedAngleUpperLowerBound = Math.PI + Math.toRadians(CURVED_BACK_TRUNK_MIN_ANGLE);
    }

    public DirectionsTranslator calculateDirections() {
        Node current, previous, next;
        int index = 0;
        previous = null;
        current = nodes.get(index);
        next = nodes.get(index + 1);

        return this;
    }

    private Actions getDirectionForNextNode(@Nullable Node prev,
                                            @NonNull Node current,
                                            @Nullable Node next) {
        // Si inizia la navigazione (il nodo precedente è nullo)
        if(prev == null) {
            if(current.isRoom()) {
                return Actions.EXIT;
            } else {
                return Actions.GO_AHEAD;
            }
        }

        // La navigazione finisce (il nodo successivo è nullo)
        if(next == null) {
            return Actions.DESTINATION_REACHED;
        }

        // Punto corrente generale
        if(current.isGeneral()) {
            return getPlaneAngleAction(prev, current, next);
        }

        return Actions.GO_AHEAD;
    }

    private Actions getPlaneAngleAction(Node prev, Node current, Node next) {
        double angle;
        TridimensionalVector prevArch, nextArc;

        prevArch = new TridimensionalVector(current.toPointF(), prev.toPointF());
        nextArc = new TridimensionalVector(current.toPointF(), next.toPointF());

        // Calcolo dell'angolo
        angle = prevArch.getPlaneAngle(nextArc);

        // Definizione dell'azione a partire a seconda dell'angolo
        if (straightAngleLowerBound < angle && angle < straightAngleUpperBound) {
            return Actions.GO_AHEAD;
        } else if (angle < curvedAngleLowerUpperBound || curvedAngleUpperLowerBound < angle) {
            if (angle < Math.PI) {
                return Actions.TURN_BACK_RIGHT;
            } else {
                return Actions.TURN_BACK_LEFT;
            }
        } else {
            if (angle < Math.PI) {
                return Actions.TURN_RIGHT;
            } else {
                return Actions.TURN_LEFT;
            }
        }
    }

    private Actions getStaisActon(Node current, Node next) {
        int currentFloor, nextFloor;

        currentFloor = Integer.parseInt(current.getFloor());
        nextFloor = Integer.parseInt(next.getFloor());

        if(nextFloor > currentFloor) {
            return Actions.GO_UPSTAIRS;
        } else {
            return Actions.GO_DOWNSTAIRS;
        }
    }

    public List<Integer> getDirections() {
        return directions;
    }
}
