package com.dii.ids.application.main.navigation;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.utils.dijkstra.Solution;
import com.dii.ids.application.utils.directions.Actions;
import com.dii.ids.application.utils.directions.DirectionsTranslator;
import com.dii.ids.application.utils.directions.HumanDirection;
import com.dii.ids.application.utils.units.Tuple;
import com.dii.ids.application.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavigatorFragment#newInstance} factory method to create an
 * instance of this fragment.
 */
public class NavigatorFragment extends Fragment {

    private static final String ORIGIN = "origine";
    private static final String DESTINATION = "destinazione";
    private static final String PIANTINE = "piantine";
    private static final String SOLUTION = "solution";
    private ViewHolder holder;
    private Node origin;
    private Node destination;
    private HashMap<String, Bitmap> piantine;
    private ArrayList<Node> solution;
    private HashMap<String, List<Node>> multiFloorSolution;
    private List<Actions> actions;
    private DirectionsTranslator translator;

    private enum ButtonType {NEXT, PREVIOUS};

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param origin            Nodo di origine
     * @param destination       Nodo di destinazione
     * @param piantine          Bitmap delle piantine
     * @param solution Lista di nodi che costiuiscono la soluzione
     * @return A new instance of fragment NavigatorFragment.
     */
    public static NavigatorFragment newInstance(Node origin,
                                                Node destination,
                                                HashMap<String, Bitmap> piantine,
                                                ArrayList<Node> solution) {
        NavigatorFragment fragment = new NavigatorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ORIGIN, origin);
        args.putSerializable(DESTINATION, destination);
        args.putSerializable(PIANTINE, piantine);
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
            piantine = (HashMap<String, Bitmap>) getArguments().getSerializable(PIANTINE);
            solution = (ArrayList<Node>) getArguments().getSerializable(SOLUTION);

            multiFloorSolution = Solution.getSolutionDividedByFloor(solution);
            translator = new DirectionsTranslator(getContext(), solution);
            actions = translator.calculateDirections()
                    .getDirections();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_navigator_fragment, container, false);
        holder = new ViewHolder(view);
        holder.setupUI();

        holder.mapView.setOrigin(origin);
        holder.mapView.setDestination(destination);
        holder.mapView.setPiantine(piantine);
        holder.mapView.setMultiFloorPath(multiFloorSolution);

        updateDirectionDisplay(new Tuple<>(0, 1));

        return view;
    }

    private class IndicationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ButtonType tag = (ButtonType) v.getTag();
            Tuple<Integer, Integer> indexes = new Tuple<>(0,0);
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
            String text = String.format(getString(R.string.toward_node), solution.get(indexes.y).getName());
            holder.nextNodeTextView.setText(text);
        } else {
            holder.nextNodeTextView.setText(getString(R.string.congratulation));
        }

    }

    public class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final MapView mapView;
        public final ImageButton nextButton;
        public final ImageButton previousButton;
        public final TextView indicationTextView;
        public final TextView nextNodeTextView;
        public final ImageView indicationSymbol;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_standard_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
            mapView = (MapView) view.findViewById(R.id.navigation_map);
            nextButton = (ImageButton) view.findViewById(R.id.next_button);
            previousButton = (ImageButton) view.findViewById(R.id.previous_button);
            indicationTextView = (TextView) view.findViewById(R.id.indication_text);
            nextNodeTextView = (TextView) view.findViewById(R.id.next_node_text);
            indicationSymbol = (ImageView) view.findViewById(R.id.indication_icon);

        }

        private void setupUI() {
            // Setup Up button on Toolbar
            NavigationActivity activity = (NavigationActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            assert activity.getSupportActionBar() != null;
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarTitle.setText(getString(R.string.title_navigator));

            nextButton.setTag(ButtonType.NEXT);
            previousButton.setTag(ButtonType.PREVIOUS);
            nextButton.setOnClickListener(new IndicationButtonListener());
            previousButton.setOnClickListener(new IndicationButtonListener());
        }
    }

}
