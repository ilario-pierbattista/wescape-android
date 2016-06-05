package com.dii.ids.application.navigation.directions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dii.ids.application.navigation.Checkpoint;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.navigation.algebra.TridimensionalVector;

// @TODO Fixare e testare (unit tests)
public class DirectionsTranslator {
    private static final double EAST = 0.0;
    private static final double NORTH_EAST = 45.0;
    private static final double NORTH_WEST = 135.0;
    private static final double WEST = 180;
    private static final double SOUTH = 270;
    private static final double SOUTH_EAST = 360;

    private static final double RIGHT_ANGLE = 90.0;
    private static final double ROUND_ANGLE = 360.0;

    private Path nodes;
    private Directions directions;
    private double offset;

    /**
     * Costruttore
     *
     * @param path Lista dei nodi che costituisce un percorso
     */
    public DirectionsTranslator(Path path) {
        this.nodes = path;
        this.directions = new Directions();
    }

    /**
     * Calcola le indicazioni
     *
     * @return Istanza corrente di {@link DirectionsTranslator}
     */
    public DirectionsTranslator calculateDirections() {
        Checkpoint previous, current, next;
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
    public Actions getDirectionForNextNode(@Nullable Checkpoint prev,
                                            @NonNull Checkpoint current,
                                            @Nullable Checkpoint next) {
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
            return getStairsAction(current, next);
        }

        // Punto corrente generale
        if (current.isGeneral()) {
            return getPlaneAngleAction(current, next);
        }

        return Actions.GO_AHEAD;
    }

    /**
     * Indicazioni per la salita delle scale
     *
     * @param current Nodo corrente
     * @param next    Nodo successivo
     * @return Indicazione
     */
    private Actions getStairsAction(Checkpoint current, Checkpoint next) {
        int currentFloor, nextFloor;

        currentFloor = Integer.parseInt(current.getFloor());
        nextFloor = Integer.parseInt(next.getFloor());

        if (nextFloor > currentFloor) {
            return Actions.GO_UPSTAIRS;
        } else {
            return Actions.GO_DOWNSTAIRS;
        }
    }

    private void addOffset(double offset) {
        this.offset += offset;
    }

    private void calcOffset (double angle, Actions action) {
        switch (action){
            case GO_AHEAD: this.addOffset(angle);
                break;
            case TURN_RIGHT:
            case TURN_BACK_RIGHT: this.addOffset(RIGHT_ANGLE - angle);
                break;
            case TURN_LEFT:
            case TURN_BACK_LEFT: this.addOffset(-RIGHT_ANGLE);
                break;
        }
    }


    private double adjustAngle (double angle){
        if (angle < 0) {
            angle += ROUND_ANGLE;
        }

        else if (angle > 360) {
            angle -= ROUND_ANGLE;
        }

        return angle;
    }

    private Actions getPlaneAngleAction(Checkpoint current, Checkpoint next) {
        double angle;
        Actions action = null;

        // Calcolo dell'angolo
        angle = TridimensionalVector.calcRotationAngle(current, next);
        angle += this.getOffset();
        angle = adjustAngle(angle);

        if (angle > NORTH_EAST && angle <= NORTH_WEST)
            action = Actions.GO_AHEAD;
        else if (angle >= EAST && angle <= NORTH_EAST)
            action = Actions.TURN_RIGHT;
        else if (angle > SOUTH && angle <= SOUTH_EAST)
            action = Actions.TURN_BACK_RIGHT;
        else if (angle > WEST && angle <= SOUTH)
            action = Actions.TURN_BACK_LEFT;
        else if (angle > NORTH_WEST && angle <= WEST)
            action = Actions.TURN_LEFT;

        calcOffset(angle, action);
        return action;
    }

    public double getOffset() {
        return offset;
    }


    public Directions getDirections() {
        return directions;
    }
}
