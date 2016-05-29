package com.dii.ids.application.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Map;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.utils.units.Tuple;
import com.dii.ids.application.views.exceptions.DestinationNotSettedException;
import com.dii.ids.application.views.exceptions.OriginNotSettedException;
import com.dii.ids.application.views.exceptions.PiantineNotSettedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MapView extends LinearLayout {
    private static final String TAG = MapView.class.getName();
    private ViewHolder holder;
    private String currentFloor;
    private Node origin, destination;
    private HashMap<String, Path> route;
    private Path orderedSolution;
    private int currentNode;
    private MaterialDialog dialog;

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
            changeImage(currentFloor);
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
     * @return MapView
     *
     * @throws PiantineNotSettedException
     */
    public MapView setDestination(Node destination) throws PiantineNotSettedException {
        this.destination = destination;
        this.currentFloor = destination.getFloor();
        try {
            changeImage(currentFloor);
            drawPins();
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    /**
     * Disegna i pin
     */
    private void drawPins() {
        holder.pinView.resetPins();

        ArrayList<MapPin> pins = new ArrayList<>();

        if (destination != null) {
            currentFloor = destination.getFloor();
            pins.add(new MapPin(destination.toPointF(), MapPin.Colors.BLUE));
        }
        // Se l'origine è impostata, ha priorità sulla destinazione e ne sovrascrive il piano corrente
        if (origin != null) {
            if (!currentFloor.equals(origin.getFloor())) {
                pins = new ArrayList<>();
                currentFloor = origin.getFloor();
            }
            pins.add(new MapPin(origin.toPointF(), MapPin.Colors.RED));
        }

        holder.pinView.setMultiplePins(pins);
    }

    /**
     * Disegna il percorso sulla mappa
     *
     * @param route Soluzione
     * @return MapView
     * @throws PiantineNotSettedException
     * @throws OriginNotSettedException
     * @throws DestinationNotSettedException
     */
    public MapView drawRoute(MultiFloorPath route) throws PiantineNotSettedException, OriginNotSettedException, DestinationNotSettedException {
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
            changeImage(currentFloor);
            holder.pinView.setPath(this.route.get(currentFloor));
            drawPins();
            setupFloorButtons();
            orderedSolution = route.toPath();
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    /**
     * Update the PinView Map image based on the floor. Handles all recylcing bitmap problems
     * N.B. Metodo fortemente scorretto: viene delegata alla vista un parte di logica. La view non dovrebbe neanche
     * sapere cos'è un thread! Purtroppo al momento non si siamo riusciti a gestire diversamente
     * il gargabe collector e le bitmap
     *
     * @param floor Floor
     */
    private void changeImage(String floor) {
        //TODO: bisognerebbe mettere un placeholder finche non si arriva al successo del task
        MapsDownloaderTask mapsDownloaderTask = new MapsDownloaderTask(getContext(), new MapListener());
        mapsDownloaderTask.execute(Integer.valueOf(floor));
    }

    /**
     * Imposta i listener sui bottoni dei piani e li nasconde se non contenuti nella soluzione
     */
    private void setupFloorButtons() {
        // UI operations
        holder.floorButtonContainer.setVisibility(View.VISIBLE);
        for (String key : holder.floorButtons.keySet()) {
            holder.floorButtons.get(key).setVisibility(View.GONE);
        }
        setupFloorButtonsUI();


        // Soluzione per piano non vuota -> visualizzare il piano
        // Soluzione per piano con un solo punto
        //              -> destinazione => visualizzare il piano
        //              -> != destinazione => nascondere il piano
        // Soluzione per piano vuota -> nascondere il piano
        Set<String> pianiNellaSoluzione = route.keySet();
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
     * Listener che gestisce il click sui bottoni dei piani
     */
    private class FloorButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String floor = button.getText().toString();

            currentFloor = floor;
            setupFloorButtonsUI();

            changeImage(currentFloor);

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

    /**
     * Imposta la selezione sui bottoni di piano
     */
    private void setupFloorButtonsUI() {
        for (String key : holder.floorButtons.keySet()) {
            holder.floorButtons.get(key).setTextColor(getResources().getColor(R.color.black));
        }
        holder.floorButtons.get(currentFloor).setTextColor(getResources().getColor(R.color.linkText));
    }

    /**
     * Callback del MapDownloaderTask
     */
    private class MapListener implements TaskListener<Map> {

        @Override
        public void onTaskSuccess(Map map) {
            holder.pinView.setImage(map.getImage());
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Map download error: ", e);
        }

        @Override
        public void onTaskComplete() {
        }

        @Override
        public void onTaskCancelled() {

        }
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
}
