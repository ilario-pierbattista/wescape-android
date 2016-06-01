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
import com.dii.ids.application.navigation.directions.Directions;
import com.dii.ids.application.navigation.directions.DirectionsTranslator;
import com.dii.ids.application.views.MapView;
import com.dii.ids.application.views.MapViewNavigationListener;
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
    private ViewHolder holder;
    private Path routeToBeFlown;
    private Stack<Node> routeTraveled;
    private MultiFloorPath multiFloorSolution;
    private Directions directions;
    private boolean emergency;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param solution Lista di nodi che costiuiscono la soluzione
     * @return A new instance of fragment NavigatorFragment.
     */
    public static NavigatorFragment newInstance(Path solution, boolean emergency) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putSerializable(SOLUTION, solution);
        args.putBoolean(EMERGENCY, emergency);
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

            if (routeToBeFlown != null) {
                multiFloorSolution = routeToBeFlown.toMultiFloorPath();
                DirectionsTranslator translator = new DirectionsTranslator(routeToBeFlown);
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
            NavigationIndices indices = null;
            switch (tag) {
                case NEXT: {
                    // indices = holder.mapView.nextStep();
                    next();
                    break;
                }
                case PREVIOUS: {
                    // indices = holder.mapView.prevStep();
                    prev();
                    break;
                }
            }
            // updateDirectionDisplay(indices);
        }
    }

    private void next() {
        if(routeToBeFlown.size() >= 2) {
            
            routeTraveled.push((Node) routeToBeFlown.getOrigin());
            ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(new ContinuousMPSTaskListener(),
                    (Edge) routeToBeFlown.getExcludedTrunk(),
                    emergency);
            continuousMPSTask.execute((Node) routeToBeFlown.get(1),
                    (Node) routeToBeFlown.getDestination());
        } else if (routeToBeFlown.size() == 1) {
            Log.i(TAG, "Destinazione raggiunta");
        }
    }

    private void prev() {
        if(routeTraveled.size() > 0) {
            Node backStep = routeTraveled.pop();
            ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(new ContinuousMPSTaskListener(),
                    (Edge) routeToBeFlown.getExcludedTrunk(),
                    emergency);
            continuousMPSTask.execute(backStep,
                    (Node) routeToBeFlown.getDestination());
        } else {
            Log.i(TAG, "Punto di partenza");
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

    private class NavigationListener implements MapViewNavigationListener {
        private Stack<Node> visitedNodes;
        private Node visitedNode;

        public NavigationListener() {
            visitedNodes = new Stack<>();
        }

        @Override
        public void onNext() {
            // Ricalcolare il percorso togliendo un nodo
            visitedNodes.push(visitedNode);

            if(routeToBeFlown.size() > 1) {
                ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(new ContinuousMPSTaskListener(),
                        (Edge) routeToBeFlown.getExcludedTrunk(),
                        emergency);
                continuousMPSTask.execute((Node) routeToBeFlown.get(1),
                        (Node) routeToBeFlown.getDestination());
            }
        }

        @Override
        public void onPrevious() {
            // Ricalcolare il percorso aggiungendo il nodo tolto in precendeza
            if(visitedNodes.size() > 0) {
                ContinuousMPSTask continuousMPSTask = new ContinuousMPSTask(new ContinuousMPSTaskListener(),
                        (Edge) routeToBeFlown.getExcludedTrunk(),
                        emergency);
                continuousMPSTask.execute(visitedNodes.pop(),
                        (Node) routeToBeFlown.getDestination());
            }
        }

        @Override
        public void saveVisitedNode(Checkpoint visitedNode) {
            this.visitedNode = (Node) visitedNode;
        }
    }

    private class ContinuousMPSTaskListener implements TaskListener<Path> {
        @Override
        public void onTaskSuccess(Path path) {
            routeToBeFlown = path;
            try {
                Log.i(TAG, routeTraveled.toString());
                Log.i(TAG, routeToBeFlown.toString());

                holder.mapView.drawRoute(routeToBeFlown.toMultiFloorPath());
            } catch (OriginNotSettedException|DestinationNotSettedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTaskError(Exception e) {

        }

        @Override
        public void onTaskComplete() {

        }

        @Override
        public void onTaskCancelled() {

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
            nextButton.setTag(ButtonType.NEXT);
            previousButton.setTag(ButtonType.PREVIOUS);
            nextButton.setOnClickListener(new IndicationButtonListener());
            previousButton.setOnClickListener(new IndicationButtonListener());
            mapView.setNavigationListener(new NavigationListener());
        }
    }
}
