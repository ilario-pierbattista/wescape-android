package com.dii.ids.application.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dii.ids.application.R;
import com.dii.ids.application.animations.ShowProgressAnimation;
import com.dii.ids.application.entity.Map;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.NavigationIndices;
import com.dii.ids.application.navigation.Path;
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
    private MultiFloorPath route;
    private Path orderedSolution;
    private MapViewNavigationListener navigationListener;
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

    /**
     * Disegna l'origine
     *
     * @param origin Nodo di origine
     * @return Istanza di MapView
     * @throws PiantineNotSettedException
     */
    public MapView setOrigin(Node origin) throws PiantineNotSettedException {
        this.origin = origin;
        this.currentFloor = origin.getFloor();
        try {
            changeImage(currentFloor);
            drawPins(currentFloor);
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
        holder.placeholderView.setVisibility(View.GONE);
        holder.progressAnimation = new ShowProgressAnimation(
                holder.mapContainer,
                holder.downloadProgress,
                20);

        holder.progressAnimation.showProgress(true);
        MapsDownloaderTask mapsDownloaderTask = new MapsDownloaderTask(getContext(), new MapListener());
        mapsDownloaderTask.execute(Integer.valueOf(floor));
    }

    /**
     * Disegna i pin
     *
     * @param floor Piano
     */
    private void drawPins(String floor) {
        ArrayList<MapPin> pins = new ArrayList<>();

        if (origin != null && origin.getFloor().equals(floor)) {
            pins.add(new MapPin(origin.toPointF(), MapPin.Colors.RED));
        }
        if (destination != null && destination.getFloor().equals(floor)) {
            pins.add(new MapPin(destination.toPointF(), MapPin.Colors.BLUE));
        }

        holder.pinView.setMultiplePins(pins);
    }

    /**
     * Disegna la destinazione
     *
     * @param destination Nodo destinazione
     * @return MapView
     * @throws PiantineNotSettedException
     */
    public MapView setDestination(Node destination) throws PiantineNotSettedException {
        this.destination = destination;
        this.currentFloor = destination.getFloor();
        try {
            changeImage(currentFloor);
            drawPins(currentFloor);
        } catch (NullPointerException e) {
            Log.e(TAG, "Errore ", e);
            throw new PiantineNotSettedException();
        }
        return this;
    }

    /**
     * Disegna il percorso sulla mappa
     *
     * @param route Soluzione
     * @return MapView
     * @throws OriginNotSettedException
     * @throws DestinationNotSettedException
     */
    public MapView drawRoute(MultiFloorPath route)
            throws OriginNotSettedException, DestinationNotSettedException {
        this.route = route;
        this.origin = (Node) route.getOrigin();
        this.destination = (Node) route.getDestination();

        if (origin == null) {
            throw new OriginNotSettedException();
        }
        if (destination == null) {
            throw new DestinationNotSettedException();
        }

        currentFloor = origin.getFloor();

        changeImage(currentFloor);
        drawPath(currentFloor);
        drawPins(currentFloor);
        setupFloorButtons();

        orderedSolution = route.toPath();

        return this;
    }

    public MapView setNavigationListener(MapViewNavigationListener navigationListener) {
        this.navigationListener = navigationListener;
        return this;
    }

    /**
     * Disegna il percorso di un piano
     *
     * @param floor Piano
     */
    private void drawPath(String floor) {
        holder.pinView.setPath(this.route.get(floor));
    }

    /**
     * Imposta i listener sui bottoni dei piani e li nasconde se non contenuti nella soluzione
     */
    private void setupFloorButtons() {
        // UI operations
        holder.hideFloorButtons()
                .setupFloorButtonsUI(currentFloor);

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
     * Passa al nodo successivo della lista dei nodi della soluzione
     *
     * @return Tupla con l'indice del nodo successivo e successivo ancora
     */
    public NavigationIndices nextStep() {
        navigationListener.saveVisitedNode(orderedSolution.get(currentNode));

        NavigationIndices indices = orderedSolution.incrementIndices(currentNode);
        currentNode = indices.current;

        // @TODO Decidere cosa fare qui
        // triggerStepChange();
        navigationListener.onNext();
        return indices;
    }

    /**
     * Innesca il cambiamento di stato di navigazione
     */
    private void triggerStepChange() {
        Node nextNode = (Node) orderedSolution.get(currentNode);

        if (!currentFloor.equals(nextNode.getFloor())) {
            changeFloor(nextNode.getFloor());
        }

        holder.pinView.setSinglePin(new MapPin(nextNode.toPointF()));
    }

    /**
     * Cambia il piano
     *
     * @param floor Piano
     * @return Istanza corrente di MapView
     */
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
            drawPath(floor);
        }

        return this;
    }

    /**
     * Passa al nodo precedente della lista dei nodi della soluzione
     *
     * @return Tupla con l'indice del nodo precednete e precedente ancora
     */
    public NavigationIndices prevStep() {
        navigationListener.saveVisitedNode(orderedSolution.get(currentNode));

        NavigationIndices indices = orderedSolution.decrementIndices(currentNode);
        currentNode = indices.current;
        triggerStepChange();

        navigationListener.onPrevious();
        return indices;
    }

    /**
     * Listener che gestisce il click sui bottoni dei piani
     */
    private class FloorButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            currentFloor = ((Button) v).getText().toString();
            holder.setupFloorButtonsUI(currentFloor);

            changeImage(currentFloor);
            drawPath(currentFloor);
            drawPins(currentFloor);
        }
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
            holder.progressAnimation.showProgress(false);
        }

        @Override
        public void onTaskCancelled() {

        }
    }

    /**
     * View Holder
     */
    private class ViewHolder {
        public final PinView pinView;
        public final LinearLayout floorButtonContainer;
        public final HashMap<String, Button> floorButtons;
        public final ViewGroup mapContainer;
        public final ProgressBar downloadProgress;
        public final ViewGroup placeholderView;
        public ShowProgressAnimation progressAnimation;

        public ViewHolder(View view) {
            pinView = (PinView) view.findViewById(R.id.map_image);
            floorButtonContainer = (LinearLayout) view.findViewById(R.id.floor_button_container);
            mapContainer = (ViewGroup) view.findViewById(R.id.map_container);
            downloadProgress = (ProgressBar) view.findViewById(R.id.downloading_progress);
            floorButtons = getFloorButtons();
            placeholderView = (ViewGroup) view.findViewById(R.id.map_placeholder);
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

        /**
         * Nasconde i pulsanti dei piani
         *
         * @return Istanza di Viewholder
         */
        private ViewHolder hideFloorButtons() {
            floorButtonContainer.setVisibility(View.VISIBLE);
            for (String key : floorButtons.keySet()) {
                floorButtons.get(key).setVisibility(View.GONE);
            }
            return this;
        }

        /**
         * Imposta la selezione sui bottoni di piano
         */
        private ViewHolder setupFloorButtonsUI(String floor) {
            for (String key : floorButtons.keySet()) {
                floorButtons.get(key).setTextColor(getResources().getColor(R.color.black));
            }
            floorButtons.get(floor).setTextColor(getResources().getColor(R.color.linkText));

            return this;
        }
    }
}
