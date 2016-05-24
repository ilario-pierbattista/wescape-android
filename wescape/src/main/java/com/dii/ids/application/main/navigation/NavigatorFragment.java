package com.dii.ids.application.main.navigation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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
import com.dii.ids.application.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass. Use the {@link NavigatorFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class NavigatorFragment extends BaseFragment {
    public static final String TAG = NavigatorFragment.class.getName();
    private static final String ORIGIN = "origine";
    private static final String DESTINATION = "destinazione";
    private static final String PIANTINE = "piantine";
    private static final String SOLUTION = "solution";
    private ViewHolder holder;
    private Node origin;
    private Node destination;
    private HashMap<String, Bitmap> piantine;
    private Path solution;
    private MultiFloorPath multiFloorSolution;
    private Directions actions;
    private DirectionsTranslator translator;

    private enum ButtonType {NEXT, PREVIOUS}

    ;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param origin      Nodo di origine
     * @param destination Nodo di destinazione
     * @param piantine    Bitmap delle piantine
     * @param solution    Lista di nodi che costiuiscono la soluzione
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

        holder.mapView.setOrigin(origin);
        holder.mapView.setDestination(destination);
        holder.mapView.setPiantine(piantine);
        holder.mapView.setMultiFloorPath(multiFloorSolution);

        HumanDirection humanDirection = translator.getHumanDirection(actions.get(0));
        holder.indicationTextView.setText(humanDirection.getDirection());

        return view;
    }

    private class IndicationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ButtonType tag = (ButtonType) v.getTag();
            int index = 0;
            switch (tag) {
                case NEXT: {
                    index = holder.mapView.nextStep();
                    break;
                }
                case PREVIOUS: {
                    index = holder.mapView.prevStep();
                    break;
                }
            }

            HumanDirection humanDirection = translator.getHumanDirection(actions.get(index));
            holder.indicationTextView.setText(humanDirection.getDirection());
            // TODO: bisogna settare anche l'icona
        }
    }

    public class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final MapView mapView;
        public final ImageButton nextButton;
        public final ImageButton previousButton;
        public final TextView indicationTextView;
        public final ImageView indicationSymbol;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_standard_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
            mapView = (MapView) view.findViewById(R.id.navigation_map);
            nextButton = (ImageButton) view.findViewById(R.id.next_button);
            previousButton = (ImageButton) view.findViewById(R.id.previous_button);
            indicationTextView = (TextView) view.findViewById(R.id.indication_text);
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
