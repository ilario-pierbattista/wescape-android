package com.dii.ids.application.utils.directions;

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
    private List<Actions> directions;
    private final double straightAngleLowerBound,
            straightAngleUpperBound,
            curvedAngleLowerUpperBound,
            curvedAngleUpperLowerBound;

    /**
     * Costruttore
     *
     * @param nodes Lista dei nodi che costituisce un percorso
     */
    public DirectionsTranslator(List<Node> nodes) {
        this.nodes = nodes;
        this.directions = new ArrayList<>();

        straightAngleLowerBound = Math.PI - Math.toRadians(STRAIGHT_TRUNK_TOLLERANCE_ANGLE);
        straightAngleUpperBound = Math.PI + Math.toRadians(STRAIGHT_TRUNK_TOLLERANCE_ANGLE);
        curvedAngleLowerUpperBound = Math.PI - Math.toRadians(CURVED_BACK_TRUNK_MIN_ANGLE);
        curvedAngleUpperLowerBound = Math.PI + Math.toRadians(CURVED_BACK_TRUNK_MIN_ANGLE);
    }

    /**
     * Calcola le indicazioni
     *
     * @return Istanza corrente di {@link DirectionsTranslator}
     */
    public DirectionsTranslator calculateDirections() {
        Node previous, current, next;
        Actions action;

        for (int index = 0; index < nodes.size(); index++) {
            try {
                previous = nodes.get(index - 1);
            } catch (IndexOutOfBoundsException e) {
                previous = null;
            }

            current = nodes.get(index);

            try {
                next = nodes.get(index + 1);
            } catch (IndexOutOfBoundsException e) {
                next = null;
            }

            action = getDirectionForNextNode(previous, current, next);

            directions.add(action);
        }

        return this;
    }

    /**
     * Calcola le indicazioni data una tripletta di nodi
     *
     * @param prev    Nodo precedente
     * @param current Nodo corrente
     * @param next    Nodo successivo
     * @return Azione corrispondente all'indicazione
     */
    private Actions getDirectionForNextNode(@Nullable Node prev,
                                            @NonNull Node current,
                                            @Nullable Node next) {
        // Si inizia la navigazione (il nodo precedente è nullo)
        if (prev == null) {
            if (current.isRoom()) {
                return Actions.EXIT;
            } else {
                return Actions.GO_AHEAD;
            }
        }

        // La navigazione finisce (il nodo successivo è nullo)
        if (next == null) {
            return Actions.DESTINATION_REACHED;
        }

        // Dislivello tra due punti
        if (!current.getFloor().equals(next.getFloor())) {
            return getStaisActon(current, next);
        }

        // Punto corrente generale
        if (current.isGeneral()) {
            return getPlaneAngleAction(prev, current, next);
        }

        return Actions.GO_AHEAD;
    }

    /**
     * Calcola la direzione in base all'angolo tra 3 punti
     *
     * @param prev    Punto precedente
     * @param current Punto corrente
     * @param next    Punto successivo
     * @return Indicazione
     */
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

    /**
     * Indicazioni per la salita delle scale
     *
     * @param current Nodo corrente
     * @param next    Nodo successivo
     * @return Indicazione
     */
    private Actions getStaisActon(Node current, Node next) {
        int currentFloor, nextFloor;

        currentFloor = Integer.parseInt(current.getFloor());
        nextFloor = Integer.parseInt(next.getFloor());

        if (nextFloor > currentFloor) {
            return Actions.GO_UPSTAIRS;
        } else {
            return Actions.GO_DOWNSTAIRS;
        }
    }

    public List<Actions> getDirections() {
        return directions;
    }
}
