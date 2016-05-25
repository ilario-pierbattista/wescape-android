package com.dii.ids.application.navigation.directions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dii.ids.application.R;
import com.dii.ids.application.navigation.Checkpoint;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.navigation.algebra.TridimensionalVector;

public class DirectionsTranslator {
    private static final double STRAIGHT_TRUNK_TOLLERANCE_ANGLE = 45.0;
    private static final double CURVED_BACK_TRUNK_MIN_ANGLE = 130.0;
    private final double straightAngleLowerBound,
            straightAngleUpperBound,
            curvedAngleLowerUpperBound,
            curvedAngleUpperLowerBound;
    private Path nodes;
    private Context context;
    private Directions directions;

    /**
     * Costruttore
     *
     * @param path Lista dei nodi che costituisce un percorso
     */
    public DirectionsTranslator(Context context, Path path) {
        this.context = context;
        this.nodes = path;
        this.directions = new Directions();

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
    private Actions getDirectionForNextNode(@Nullable Checkpoint prev,
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
            return getPlaneAngleAction(prev, current, next);
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

    /**
     * Calcola la direzione in base all'angolo tra 3 punti
     *
     * @param prev    Punto precedente
     * @param current Punto corrente
     * @param next    Punto successivo
     * @return Indicazione
     */
    private Actions getPlaneAngleAction(Checkpoint prev, Checkpoint current, Checkpoint next) {
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

    public Directions getDirections() {
        return directions;
    }

    // @TODO Questo metodo non dovrebbe essere in HumanDirection?
    public HumanDirection getHumanDirection(Actions action) {
        HumanDirection humanDirection = new HumanDirection();
        switch (action) {
            case GO_AHEAD: {
                humanDirection.setDirection(context.getString(R.string.action_go_ahead));
                humanDirection.setIconResource(R.drawable.ic_go_ahead);
                break;
            }
            case TURN_RIGHT: {
                humanDirection.setDirection(context.getString(R.string.action_turn_right));
                humanDirection.setIconResource(R.drawable.ic_turn_right);
                break;
            }
            case TURN_LEFT: {
                humanDirection.setDirection(context.getString(R.string.action_turn_left));
                humanDirection.setIconResource(R.drawable.ic_turn_left);
                break;
            }
            case TURN_BACK_RIGHT: {
                humanDirection.setDirection(context.getString(R.string.action_turn_back_right));
                humanDirection.setIconResource(R.drawable.ic_turn_back_right);
                break;
            }
            case TURN_BACK_LEFT: {
                humanDirection.setDirection(context.getString(R.string.action_turn_back_left));
                humanDirection.setIconResource(R.drawable.ic_turn_back_left);
                break;
            }
            case GO_UPSTAIRS: {
                humanDirection.setDirection(context.getString(R.string.action_go_upstairs));
                humanDirection.setIconResource(R.drawable.ic_go_upstairs);
                break;
            }
            case GO_DOWNSTAIRS: {
                humanDirection.setDirection(context.getString(R.string.action_go_downstairs));
                humanDirection.setIconResource(R.drawable.ic_go_downstairs);
                break;
            }
            case EXIT: {
                humanDirection.setDirection(context.getString(R.string.action_exit));
                humanDirection.setIconResource(R.drawable.ic_exit);
                break;
            }
            case DESTINATION_REACHED: {
                humanDirection.setDirection(context.getString(R.string.action_destination_reached));
                humanDirection.setIconResource(R.drawable.ic_flag);
                break;
            }
        }

        return humanDirection;
    }
}
