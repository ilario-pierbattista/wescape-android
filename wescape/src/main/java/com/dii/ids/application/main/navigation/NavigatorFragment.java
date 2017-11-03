package com.dii.ids.application.main.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.directions.HumanDirection;
import com.dii.ids.application.main.navigation.tasks.ContinuousMPSTask;
import com.dii.ids.application.navigation.Checkpoint;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.NavigationIndices;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.navigation.Trunk;
import com.dii.ids.application.navigation.directions.Actions;
import com.dii.ids.application.navigation.directions.Directions;
import com.dii.ids.application.navigation.directions.DirectionsTranslator;
import com.dii.ids.application.views.MapView;
import com.dii.ids.application.views.exceptions.DestinationNotSettedException;
import com.dii.ids.application.views.exceptions.OriginNotSettedException;

import java.util.Stack;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavigatorFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class NavigatorFragment extends BaseFragment {
    public static final String TAG = NavigatorFragment.class.getName();
    private static final String SOLUTION = "solution";
    private static final String EMERGENCY = "emergency";
    private static final String OFFLINE = "offline";
    private ViewHolder holder;
    private Path routeToBeFlown;
    private Trunk excludedTrunk;
    private Stack<Node> routeTraveled;
    private MultiFloorPath multiFloorSolution;
    private Directions directions;
    private DirectionsTranslator translator;
    private boolean emergency;
    private boolean offline;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param solution Lista di nodi che costiuiscono la soluzione
     * @return A new instance of fragment NavigatorFragment.
     */
    public static NavigatorFragment newInstance(Path solution, boolean emergency, boolean offline) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putSerializable(SOLUTION, solution);
        args.putBoolean(EMERGENCY, emergency);
        args.putBoolean(OFFLINE, offline);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routeToBeFlown = (Path) getArguments().getSerializable(SOLUTION);
            routeTraveled = new Stack<>();
            emergency = getArguments().getBoolean(EMERGENCY);
            offline = getArguments().getBoolean(OFFLINE);

            if (routeToBeFlown != null) {
                excludedTrunk = routeToBeFlown.getExcludedTrunk();
                multiFloorSolution = routeToBeFlown.toMultiFloorPath();
                translator = new DirectionsTranslator(routeToBeFlown);
                directions = translator.calculateDirections()
                        .getDirections();
            } else {
                Log.e(TAG, "Solution is null. Aborting");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_navigator_fragment, container, false);
        holder = new ViewHolder(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        holder.setupUI();

        try {
            holder.mapView.drawRoute(multiFloorSolution);
        } catch (OriginNotSettedException | DestinationNotSettedException e) {
            e.printStackTrace();
        }

        updateDirectionDisplay(new NavigationIndices(0, 1));
    }

    private enum ButtonType {NEXT, PREVIOUS}

    private class IndicationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ButtonType tag = (ButtonType) v.getTag();
            switch (tag) {
                case NEXT: {
                    next();
                    break;
                }
                case PREVIOUS: {
                    prev();
                    break;
                }
            }
        }
    }

    private void next() {
        if (routeToBeFlown.size() >= 2) {
            routeTraveled.push((Node) routeToBeFlown.getOrigin());
            ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(getContext(),
                    new ContinuousMPSTaskListener(),
                    (Edge) excludedTrunk, emergency, offline);
            continuousMPSTask.execute((Node) routeToBeFlown.get(1),
                    (Node) routeToBeFlown.getDestination());
            holder.mapView.showSpinner(true);
        }
    }

    private void prev() {
        if (routeTraveled.size() > 0) {
            Node backStep = routeTraveled.pop();
            ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(getContext(),
                    new ContinuousMPSTaskListener(),
                    (Edge) excludedTrunk, emergency, offline);
            continuousMPSTask.execute(backStep,
                    (Node) routeToBeFlown.getDestination());
            holder.mapView.showSpinner(true);
        }
    }

    /**
     * Aggiorna la vista con le indicazioni da seguire
     *
     * @param indices Tupla di indici dei nodi
     */
    private void updateDirectionDisplay(NavigationIndices indices) {
        holder.setCurrentDirection(HumanDirection
                .createHumanDirection(getContext(), directions.getCurrent(indices)));
        if (!indices.isLast()) {
            holder.setNextDirection(
                    HumanDirection.createHumanDirection(getContext(), directions.getNext(indices)),
                    ((Node) routeToBeFlown.getNext(indices)).getName());
        } else {
            holder.setNavigationEnding();
        }
    }

    private class ContinuousMPSTaskListener implements TaskListener<Path> {
        private final String TAG = ContinuousMPSTask.class.getName();
        private Checkpoint prev, current, next, nexter;
        private Actions currentAction, nextAction;

        @Override
        public void onTaskSuccess(Path path) {
            routeToBeFlown = path;
            try {
                // @TODO Prendere qui il primo lato ed inviarlo al server per la posizione futura

                // Ricavo le indicazioni
                setPoints();
                currentAction = translator.getDirectionForNextNode(prev, current, next);
                holder.setCurrentDirection(HumanDirection.createHumanDirection(getContext(), currentAction));

                if (routeToBeFlown.isDestinationReached()) {
                    // Destinazione raggiunta, disegno solo il pallino rosso
                    holder.setNavigationEnding();
                    holder.mapView.reset()
                            .setOrigin((Node) routeToBeFlown.getDestination());
                } else {
                    // Destinazione da raggiungere, disegno tutto il percorso
                    nextAction = translator.getDirectionForNextNode(current, next, nexter);
                    holder.setNextDirection(HumanDirection.createHumanDirection(getContext(), nextAction),
                            ((Node) next).getName());
                    holder.mapView.drawRoute(routeToBeFlown.toMultiFloorPath());
                }
            } catch (OriginNotSettedException | DestinationNotSettedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Error", e);
        }

        @Override
        public void onTaskComplete() {
            holder.mapView.showSpinner(false);
        }

        @Override
        public void onTaskCancelled() {
            holder.mapView.showSpinner(false);
        }

        private void setPoints() {
            prev = routeTraveled.size() > 0 ? routeTraveled.lastElement() : null;
            current = routeToBeFlown.getOrigin();
            next = !routeToBeFlown.isDestinationReached() ? routeToBeFlown.get(1) : null;
            nexter = routeToBeFlown.size() > 2 ? routeToBeFlown.get(2) : null;
        }
    }

    public class ViewHolder {
        public final MapView mapView;
        public final ImageButton nextButton;
        public final ImageButton previousButton;
        public final TextView indicationTextView;
        public final TextView nextNodeTextView;
        public final ImageView indicationSymbol;
        public final ImageView nextNodeIcon;
        public final ViewGroup nextNodeContainer;

        public ViewHolder(View view) {
            mapView = (MapView) view.findViewById(R.id.navigation_map);
            nextButton = (ImageButton) view.findViewById(R.id.next_button);
            previousButton = (ImageButton) view.findViewById(R.id.previous_button);
            indicationTextView = (TextView) view.findViewById(R.id.indication_text);
            nextNodeTextView = (TextView) view.findViewById(R.id.next_node_text);
            indicationSymbol = (ImageView) view.findViewById(R.id.indication_icon);
            nextNodeIcon = (ImageView) view.findViewById(R.id.next_step_icon);
            nextNodeContainer = (ViewGroup) view.findViewById(R.id.next_step_container);
        }

        /**
         * Visualizza l'indicazione corrente
         *
         * @param direction Indicazione corrente
         * @return Istanza di corrente di {@link ViewHolder}
         */
        public ViewHolder setCurrentDirection(HumanDirection direction) {
            holder.indicationTextView.setText(direction.getDirection());
            holder.indicationSymbol.setImageResource(direction.getIconResource());
            return this;
        }

        /**
         * Visualizza l'indicazione successiva
         *
         * @param direction Indicazione successiva
         * @param nodeName  Nome del nodo successivo
         * @return Istanza corrente di {@link ViewHolder}
         */
        public ViewHolder setNextDirection(HumanDirection direction, String nodeName) {
            holder.nextNodeContainer.setVisibility(View.VISIBLE);
            String text = String.format(getString(R.string.toward_node), nodeName);
            holder.nextNodeTextView.setText(text);
            holder.nextNodeIcon.setImageResource(direction.getIconResource());
            return this;
        }

        /**
         * Visualizza il messaggio di fine navigazione
         *
         * @return Istanza corrente di {@link ViewHolder}
         */
        public ViewHolder setNavigationEnding() {
            holder.nextNodeContainer.setVisibility(View.GONE);
            holder.nextNodeTextView.setText(getString(R.string.congratulation));
            return this;
        }

        private void setupUI() {
            mapView.setOffline(offline);
            nextButton.setTag(ButtonType.NEXT);
            previousButton.setTag(ButtonType.PREVIOUS);
            nextButton.setOnClickListener(new IndicationButtonListener());
            previousButton.setOnClickListener(new IndicationButtonListener());
        }
    }
}
