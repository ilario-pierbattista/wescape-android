package com.dii.ids.application.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.utils.dijkstra.Solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;

public class MapView extends LinearLayout {
    private static final String TAG = MapView.class.getName();
    private ViewHolder holder;
    private String currentFloor;
    private Node origin, destination;
    private HashMap<String, Bitmap> piantine;
    private HashMap<String, List<Node>> multiFloorPath;
    private List<Node> orderedSolution;
    private int currentNode;

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public MapView setOrigin(Node origin) {
        this.origin = origin;
        return this;
    }

    public MapView setDestination(Node destination) {
        this.destination = destination;
        return this;
    }

    public MapView setPiantine(HashMap<String, Bitmap> piantine) {
        this.piantine = piantine;
        return this;
    }

    private void init() {
        inflate(getContext(), R.layout.map_view, this);
        holder = new ViewHolder(this);
        holder.floorButtonContainer.setVisibility(View.GONE);
    }

    public MapView changeFloor(String floor) {
        for (String key : holder.floorButtons.keySet()) {
            Button button = holder.floorButtons.get(key);
            if (floor.equals(button.getText().toString())) {
                button.performClick();
            }
        }
        drawOnMap(floor);

        return this;
    }

    public MapView changeFloor(int floor) {
        return changeFloor(Integer.toString(floor));
    }

    public void nextStep() {
        if (currentNode < orderedSolution.size() - 1) {
            currentNode++;
        }

        triggerStepChange();
    }

    public void prevStep() {
        if (currentNode > 0 && currentNode <= orderedSolution.size() - 1) {
            currentNode--;
        }

        triggerStepChange();
    }

    private void triggerStepChange() {
        Node nextNode = orderedSolution.get(currentNode);

        if (!currentFloor.equals(nextNode.getFloor())) {
            changeFloor(nextNode.getFloor());
        }

        holder.pinView.resetPins();
        holder.pinView.setSinglePin(new MapPin(nextNode.toPointF()));
    }

    /**
     * Imposta i listener sui bottoni dei piani e li nasconde se non contenuti nella soluzione
     */
    private void setupFloorButtonListener() {
        holder.floorButtonContainer.setVisibility(View.VISIBLE);
        Set<String> pianiNellaSoluzione = multiFloorPath.keySet();

        holder.floorButtons.get(currentFloor).setTextColor(getResources().getColor(R.color.linkText));
        for (String key : holder.floorButtons.keySet()) {
            holder.floorButtons.get(key).setVisibility(View.GONE);
        }

        // Soluzione per piano non vuota -> visualizzare il piano
        // Soluzione per piano con un solo punto
        //              -> destinazione => visualizzare il piano
        //              -> != destinazione => nascondere il piano
        // Soluzione per piano vuota -> nascondere il piano

        List<Node> solutionPerFloor;
        boolean onePointSolution, destinationSolution, multiplePointSolution, originSolution;

        for (String floor : pianiNellaSoluzione) {
            solutionPerFloor = multiFloorPath.get(floor);

            onePointSolution = solutionPerFloor.size() == 1;
            multiplePointSolution = solutionPerFloor.size() > 1;
            destinationSolution = (onePointSolution && solutionPerFloor.get(0).equals(destination));
            originSolution = (onePointSolution && solutionPerFloor.get(0).equals(origin));

            if (multiplePointSolution || destinationSolution || originSolution) {
                holder.floorButtons.get(floor).setVisibility(View.VISIBLE);
                holder.floorButtons.get(floor).setOnClickListener(new FloorButtonListener());
            }
        }
    }

    /**
     * Imposta i path divisi per piano
     *
     * @param multiFloorPath Hashmap di percorsi. La chiave è il nome del piano, il valore è una lista di nodi connessi
     *                       che costituiscono il percorso
     * @return Istanza corrente di MapView
     */
    public MapView setMultiFloorPath(HashMap<String, List<Node>> multiFloorPath) {
        this.multiFloorPath = multiFloorPath;
        currentFloor = origin.getFloor();
        Log.i(TAG, "Piano corrente " + currentFloor);
        Bitmap mapImage = piantine.get(currentFloor);
        holder.pinView.setImage(mapImage.copy(mapImage.getConfig(), true));

        setupFloorButtonListener();
        drawOnMap(currentFloor);
        orderedSolution = Solution.getOrderedSolution(origin, destination, multiFloorPath);
        return this;
    }

    /**
     * Disegna i pin ed il percorso di un piano
     *
     * @param floor Piano
     * @return Istanza di MapView
     */
    private MapView drawOnMap(String floor) {
        ArrayList<MapPin> pins = new ArrayList<>();

        if (origin != null && origin.getFloor().equals(floor)) {
            pins.add(new MapPin(origin.toPointF(), MapPin.Colors.RED));
        }
        if (destination != null && destination.getFloor().equals(floor)) {
            pins.add(new MapPin(destination.toPointF(), MapPin.Colors.BLUE));
        }
        holder.pinView.setMultiplePins(pins);
        if (multiFloorPath != null) {
            holder.pinView.setPath(multiFloorPath.get(floor));
        }

        return this;
    }

    /**
     * Listener dei pulsanti corrispondenti ai piani
     */
    private class FloorButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String floor = button.getText().toString();
            button.setTextColor(getResources().getColor(R.color.linkText));

            if (!floor.equals(currentFloor)) {
                currentFloor = floor;
                setFloorButtonState();

                Bitmap map = piantine.get(floor);
                Bitmap mapCopy = map.copy(map.getConfig(), true);
                Log.i("Cambio immagine", "Cambio");
                holder.pinView.setImage(mapCopy);

                MapPin originPin = new MapPin(origin.toPointF(), MapPin.Colors.RED);
                MapPin destinationPin = new MapPin(destination.toPointF(), MapPin.Colors.BLUE);

                boolean isOrigin = floor.equals(origin.getFloor());
                boolean isDestination = floor.equals(destination.getFloor());
                if (isOrigin) {
                    holder.pinView.setSinglePin(originPin);
                } else if (isDestination) {
                    holder.pinView.setSinglePin(destinationPin);
                } else {
                    holder.pinView.resetPins();
                }

                holder.pinView.setPath(multiFloorPath.get(floor));
            }
        }
    }

    /**
     * Imposta lo stato di selezione del pulsante corrispondente al piano corrente
     */
    private void setFloorButtonState() {
        for (String key : holder.floorButtons.keySet()) {
            holder.floorButtons.get(key).setTextColor(getResources().getColor(R.color.black));
        }
        holder.floorButtons.get(currentFloor)
                .setTextColor(getResources().getColor(R.color.linkText));
    }

    private static class ViewHolder {
        public final PinView pinView;
        public final LinearLayout floorButtonContainer;
        public final HashMap<String, Button> floorButtons;

        public ViewHolder(View view) {
            pinView = (PinView) view.findViewById(R.id.map_image);
            floorButtonContainer = (LinearLayout) view.findViewById(R.id.floor_button_container);
            floorButtons = getFloorButtons();
        }

        /**
         * Ritorna la lista dei bottoni dei piani
         *
         * @return Hashmap di bottoni
         */
        private HashMap<String, Button> getFloorButtons() {
            ViewGroup buttonContainer = floorButtonContainer;
            HashMap<String, Button> result = new HashMap<>();

            for (int i = 0; i < buttonContainer.getChildCount(); i++) {
                View v = buttonContainer.getChildAt(i);
                if (v instanceof Button) {
                    Button button = (Button) v;
                    result.put(button.getText().toString(), button);
                }
            }

            return result;
        }
    }
}
