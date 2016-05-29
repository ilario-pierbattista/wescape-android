package com.dii.ids.application.main.navigation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.navigation.directions.Directions;
import com.dii.ids.application.navigation.directions.DirectionsTranslator;
import com.dii.ids.application.navigation.directions.HumanDirection;
import com.dii.ids.application.utils.units.Tuple;
import com.dii.ids.application.views.MapView;
import com.dii.ids.application.views.exceptions.DestinationNotSettedException;
import com.dii.ids.application.views.exceptions.OriginNotSettedException;
import com.dii.ids.application.views.exceptions.PiantineNotSettedException;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavigatorFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class NavigatorFragment extends BaseFragment {
    public static final String TAG = NavigatorFragment.class.getName();
    private static final String ORIGIN = "origine";
    private static final String DESTINATION = "destinazione";
    private static final String SOLUTION = "solution";
    private ViewHolder holder;
    private Node origin;
    private Node destination;
    private HashMap<String, Bitmap> piantine;
    private Path solution;
    private MultiFloorPath multiFloorSolution;
    private Directions actions;
    private DirectionsTranslator translator;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param origin      Nodo di origine
     * @param destination Nodo di destinazione
     * @param solution    Lista di nodi che costiuiscono la soluzione
     * @return A new instance of fragment NavigatorFragment.
     */
    public static NavigatorFragment newInstance(Node origin,
                                                Node destination,
                                                Path solution) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ORIGIN, origin);
        args.putSerializable(DESTINATION, destination);
        args.putSerializable(SOLUTION, solution);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            origin = (Node) getArguments().getSerializable(ORIGIN);
            destination = (Node) getArguments().getSerializable(DESTINATION);
            solution = (Path) getArguments().getSerializable(SOLUTION);

            if (solution != null) {
                multiFloorSolution = solution.toMultiFloorPath();
                translator = new DirectionsTranslator(getContext(), solution);
                actions = translator.calculateDirections()
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
        holder.setupUI();

        try {
            holder.mapView.setOrigin(origin)
                    .setDestination(destination)
                    .drawRoute(multiFloorSolution);
        } catch (PiantineNotSettedException | OriginNotSettedException | DestinationNotSettedException e) {
            e.printStackTrace();
        }

        updateDirectionDisplay(new Tuple<>(0, 1));

        return view;
    }

    private enum ButtonType {NEXT, PREVIOUS}

    private class IndicationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ButtonType tag = (ButtonType) v.getTag();
            Tuple<Integer, Integer> indexes = new Tuple<>(0, 0);
            switch (tag) {
                case NEXT: {
                    indexes = holder.mapView.nextStep();
                    break;
                }
                case PREVIOUS: {
                    indexes = holder.mapView.prevStep();
                    break;
                }
            }

            updateDirectionDisplay(indexes);
        }
    }

    /**
     * Aggiorna la vista con le indicazioni da seguire
     *
     * @param indexes Tupla di indici dei nodi
     */
    private void updateDirectionDisplay(Tuple<Integer, Integer> indexes) {
        HumanDirection humanDirection = translator.getHumanDirection(actions.get(indexes.x));
        holder.indicationTextView.setText(humanDirection.getDirection());
        holder.indicationSymbol.setImageResource(humanDirection.getIconResource());
        if (!indexes.x.equals(indexes.y)) {
            Node node = (Node) solution.get(indexes.y);
            holder.nextNodeContainer.setVisibility(View.VISIBLE);
            String text = String.format(getString(R.string.toward_node), node.getName());
            holder.nextNodeTextView.setText(text);
            HumanDirection humanDirection1 = translator.getHumanDirection(actions.get(indexes.y));
            holder.nextNodeIcon.setImageResource(humanDirection1.getIconResource());
        } else {
            holder.nextNodeContainer.setVisibility(View.GONE);
            holder.nextNodeTextView.setText(getString(R.string.congratulation));
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

        private void setupUI() {
            nextButton.setTag(ButtonType.NEXT);
            previousButton.setTag(ButtonType.PREVIOUS);
            nextButton.setOnClickListener(new IndicationButtonListener());
            previousButton.setOnClickListener(new IndicationButtonListener());
        }
    }

}
