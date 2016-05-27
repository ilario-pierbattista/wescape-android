package com.dii.ids.application.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.utils.units.Tuple;
import com.dii.ids.application.views.exceptions.DestinationNotSettedException;
import com.dii.ids.application.views.exceptions.OriginNotSettedException;
import com.dii.ids.application.views.exceptions.PiantineNotSettedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapView extends LinearLayout {
    private static final String TAG = MapView.class.getName();
    private ViewHolder holder;
    private String currentFloor;
    private Node origin, destination;
    private HashMap<String, Bitmap> piantine;
    private HashMap<String, Path> route;
    private Path orderedSolution;
    private int currentNode;

    public MapView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.map_view, this);
        holder = new ViewHolder(this);
        holder.floorButtonContainer.setVisibility(View.GONE);
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

    /**
     * Disegna l'origine
     *
     * @param origin Nodo di origine
     * @return
     *
     * @throws PiantineNotSettedException
     */
    public MapView setOrigin(Node origin) throws PiantineNotSettedException {
        this.origin = origin;
        this.currentFloor = origin.getFloor();
        try {
            Log.i(TAG, piantine.toString());
            Bitmap mapImage = piantine.get(origin.getFloor());
            holder.pinView.setImage(mapImage.copy(mapImage.getConfig(), true));
            drawPins();
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    /**
     * Disegna la destinazione
     *
     * @param destination Nodo destinazione
     * @return
     *
     * @throws PiantineNotSettedException
     */
    public MapView setDestination(Node destination) throws PiantineNotSettedException {
        this.destination = destination;
        this.currentFloor = destination.getFloor();
        try {
            holder.pinView.setImage(piantine.get(destination.getFloor()));
            drawPins();
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    public MapView drawRoute(MultiFloorPath route)
            throws
            PiantineNotSettedException,
            OriginNotSettedException,
            DestinationNotSettedException {
        this.route = route;
        this.origin = (Node) route.getOrigin();
        this.destination = (Node) route.getDestination();

        if (origin == null) {
            throw new OriginNotSettedException();
        }
        if (destination == null) {
            throw new DestinationNotSettedException();
        }

        try {
            currentFloor = origin.getFloor();
            Bitmap mapImage = piantine.get(currentFloor);
            holder.pinView.setImage(mapImage.copy(mapImage.getConfig(), true));
            holder.pinView.setPath(this.route.get(currentFloor));
            drawPins();
            setupFloorButtonListener();
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    private void drawPins() {
        ArrayList<MapPin> pins = new ArrayList<>();

        if (destination != null) {
            currentFloor = destination.getFloor();
            pins.add(new MapPin(destination.toPointF(), MapPin.Colors.BLUE));
        }
        // Se l'origine è impostata, ha priorità sulla destinazione e ne sovrascrive il piano corrente
        if (origin != null) {
            if(!currentFloor.equals(origin.getFloor())) {
                pins = new ArrayList<>();
                currentFloor = origin.getFloor();
            }
            pins.add(new MapPin(origin.toPointF(), MapPin.Colors.RED));
        }

        holder.pinView.setMultiplePins(pins);
    }

    @Deprecated
    public MapView setOriginDummy(Node origin) {
        this.origin = origin;
        return this;
    }

    @Deprecated
    public MapView setDestinationDummy(Node destination) {
        this.destination = destination;
        return this;
    }

    public MapView setPiantine(HashMap<String, Bitmap> piantine) {
        this.piantine = piantine;
        return this;
    }

    @Deprecated
    public MapView changeFloor(int floor) {
        return changeFloor(Integer.toString(floor));
    }

    @Deprecated
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
        if (route != null) {
            holder.pinView.setPath(route.get(floor));
        }

        return this;
    }

    /**
     * Passa al nodo successivo della lista dei nodi della soluzione
     *
     * @return Tupla con l'indice del nodo successivo e successivo ancora
     */
    @Deprecated
    public Tuple<Integer, Integer> nextStep() {
        int nextNode;
        if (currentNode < orderedSolution.size() - 1) {
            currentNode++;
            if (!(currentNode == orderedSolution.size() - 1)) {
                nextNode = currentNode + 1;
            } else {
                nextNode = currentNode;
            }
        } else {
            nextNode = currentNode;
        }

        triggerStepChange();
        return new Tuple<>(currentNode, nextNode);
    }

    private void triggerStepChange() {
        Node nextNode = (Node) orderedSolution.get(currentNode);

        if (!currentFloor.equals(nextNode.getFloor())) {
            changeFloor(nextNode.getFloor());
        }

        holder.pinView.resetPins();
        holder.pinView.setSinglePin(new MapPin(nextNode.toPointF()));
    }

    /**
     * Passa al nodo precedente della lista dei nodi della soluzione
     *
     * @return Tupla con l'indice del nodo precednete e precedente ancora
     */
    @Deprecated
    public Tuple<Integer, Integer> prevStep() {
        if (currentNode > 0 && currentNode <= orderedSolution.size() - 1) {
            currentNode--;
        }
        int nextNode = currentNode + 1;

        triggerStepChange();
        return new Tuple<>(currentNode, nextNode);
    }

    /**
     * Imposta i path divisi per piano
     *
     * @param route Hashmap di percorsi. La chiave è il nome del piano, il valore è una lista di nodi connessi che
     *              costituiscono il percorso
     * @return Istanza corrente di MapView
     */
    @Deprecated
    public MapView setRoute(MultiFloorPath route) {
        this.route = route;
        currentFloor = origin.getFloor();
        Log.i(TAG, "Piano corrente " + currentFloor);
        Bitmap mapImage = piantine.get(currentFloor);
        holder.pinView.setImage(mapImage.copy(mapImage.getConfig(), true));

        setupFloorButtonListener();
        drawOnMap(currentFloor);
        orderedSolution = route.toPath();
        return this;
    }

    /**
     * Imposta i listener sui bottoni dei piani e li nasconde se non contenuti nella soluzione
     */
    private void setupFloorButtonListener() {
        holder.floorButtonContainer.setVisibility(View.VISIBLE);
        Set<String> pianiNellaSoluzione = route.keySet();

        holder.floorButtons.get(currentFloor).setTextColor(getResources().getColor(R.color.linkText));
        for (String key : holder.floorButtons.keySet()) {
            holder.floorButtons.get(key).setVisibility(View.GONE);
        }

        // Soluzione per piano non vuota -> visualizzare il piano
        // Soluzione per piano con un solo punto
        //              -> destinazione => visualizzare il piano
        //              -> != destinazione => nascondere il piano
        // Soluzione per piano vuota -> nascondere il piano

        Path solutionPerFloor;
        boolean onePointSolution, destinationSolution, multiplePointSolution, originSolution;

        for (String floor : pianiNellaSoluzione) {
            solutionPerFloor = route.get(floor);

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

                holder.pinView.setPath(route.get(floor));
            }
        }
    }
}
