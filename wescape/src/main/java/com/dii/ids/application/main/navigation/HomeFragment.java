package com.dii.ids.application.main.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dii.ids.application.R;
import com.dii.ids.application.animations.FabAnimation;
import com.dii.ids.application.animations.ToolbarAnimation;
import com.dii.ids.application.entity.Edge;
import com.dii.ids.application.entity.Map;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.DownloadEdgesTask;
import com.dii.ids.application.main.navigation.tasks.DownloadMapsTask;
import com.dii.ids.application.main.navigation.tasks.DownloadNodesTask;
import com.dii.ids.application.main.navigation.tasks.MinimumPathTask;
import com.dii.ids.application.main.navigation.views.MapPin;
import com.dii.ids.application.main.navigation.views.PinView;
import com.dii.ids.application.utils.dijkstra.Solution;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.util.examples.maze.Maze2D;

/**
 * HomeFragment: classe per la schermata principale nel contesto di navigazione.
 */
public class HomeFragment extends BaseFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String INTENT_KEY_POSITION = "position";
    private static String originText;
    private static String destinationText;
    private static Node origin = null, destination = null;
    private List<List<Node>> paths = null;
    private List<Node> optimalPath = null;
    private ViewHolder holder;
    private boolean emergency = false;
    private DownloadMapsTask downloadMapsTask;
    private Bitmap mapImage;
    private MinimumPathTask minimumPathTask;
    private String currentFloor = null;

    private HashMap<String, Bitmap> piantine;
    private HashMap<String, List<Node>> percorsoOttimoPerPiano;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Node node;
        byte[] serializedData;

        try {
            serializedData = data.getByteArrayExtra(INTENT_KEY_POSITION);
            if (serializedData == null) {
                throw new NullPointerException("Null array data");
            }
            node = (Node) SerializationUtils.deserialize(serializedData);

            switch (requestCode) {
                case ORIGIN_SELECTION_REQUEST_CODE:
                    origin = node;
                    break;
                case DESTINATION_SELECTION_REQUEST_CODE:
                    destination = node;
                    break;
            }
        } catch (NullPointerException ee) {
            Log.e(TAG, "NullPointer", ee);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.navigation_home_fragment, container, false);
        holder = new ViewHolder(view);

        originText = originText == null ? getString(R.string.navigation_select_origin) : originText;
        destinationText = destinationText == null ? getString(R.string.navigation_select_destination) : destinationText;

        setupViewUI();
        downloadMaps();

        DownloadNodesTask downloadNodesTask = new DownloadNodesTask(getContext(), new NodesDownloaderListener());
        downloadNodesTask.execute();

        return view;
    }

    private void setupViewUI() {
        holder.originViewPlaceholder.setText(R.string.navigation_starting_from);
        holder.originViewIcon.setImageResource(R.drawable.ic_my_location);
        holder.destinationViewPlaceholder.setText(R.string.navigation_going_to);
        holder.destinationViewIcon.setImageResource(R.drawable.ic_pin_drop);

        originText = origin == null ?
                getString(R.string.navigation_select_origin) :
                origin.getName();
        destinationText = destination == null ?
                getString(R.string.navigation_select_destination) :
                destination.getName();

        holder.originViewText.setText(originText);
        holder.destinationViewText.setText(destinationText);

        // Setup listeners
        holder.originView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectionFragment(v);
            }
        });

        holder.startFabButton.setOnClickListener(new NavigationButtonListener());

        if (paths == null) {
            holder.pathsFabButton.hide();
        }

        holder.pathsFabButton.setOnClickListener(new PathButtonListener());

        if (emergency) {
            holder.revealView.setBackgroundColor(color(R.color.regularRed));
            holder.revealBackgroundView.setBackgroundColor(color(R.color.regularRed));
            holder.startFabButton.setBackgroundTintList(ColorStateList.valueOf(color(R.color.regularRed)));
            holder.toolbarTitle.setText(R.string.action_emergency);
            holder.destinationViewText.setText(R.string.description_destination_emergency);
            holder.destinationView.setClickable(false);

        } else {
            holder.destinationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSelectionFragment(v);
                }
            });
            holder.destinationView.setClickable(true);
        }

        downloadMapsTask = new DownloadMapsTask(getContext(), new MapsDownloaderListener());
        if (origin != null) {
            if (destination != null) {
                minimumPathTask = new MinimumPathTask(getContext(), new MinimumPathListener());
                minimumPathTask.execute(origin, destination);
            } else {
                downloadMapsTask.execute(Integer.parseInt(origin.getFloor()));
            }
        } else {
            downloadMapsTask.execute(STARTING_FLOOR);
        }

        holder.floorButtonContainer.setVisibility(View.GONE);
    }

    /**
     * Scarica tutte le mappe e le salva nell'HashMap dove la chiave è il piano
     */
    private void downloadMaps() {
        if (piantine == null) {
            piantine = new HashMap<>();
        }

        //TODO: rendere l'array di piani costanti globali
        int[] piani = {145, 150, 155};
        for (int piano : piani) {
            DownloadMapsTask task = new DownloadMapsTask(getContext(), new MapListener());
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, piano);
        }
    }

    /**
     * Listner che riempie la hasmap delle piantine
     */
    private class MapListener implements TaskListener<Map> {
        @Override
        public void onTaskSuccess(Map map) {
            Log.i("Piantina", map.getImage().toString());
            piantine.put(map.getFloor(), map.getImage());
            Log.i("Piantina", String.valueOf(piantine.size()));
        }

        @Override
        public void onTaskError(Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_network_download_image),
                           Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskComplete() {

        }

        @Override
        public void onTaskCancelled() {

        }
    }

    private void openSelectionFragment(View v) {
        SelectionFragment selectionFragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int code = 1;
        switch (v.getId()) {
            case R.id.navigation_input_origin:
                code = ORIGIN_SELECTION_REQUEST_CODE;
                break;
            case R.id.navigation_input_destination:
                code = DESTINATION_SELECTION_REQUEST_CODE;
                break;
        }

        selectionFragment = SelectionFragment.newInstance(code);
        selectionFragment.setTargetFragment(this, code);

        fragmentTransaction.replace(R.id.navigation_content_pane, selectionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    private void openNavigatorFragment() {
        NavigatorFragment navigatorFragment = new NavigatorFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.navigation_content_pane, navigatorFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set the Toolbar as the ActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(holder.toolbar);
        assert activity.getSupportActionBar() != null;
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_navigation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_emergency:
                toggleEmergency();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update view for normal/emergency state
     */
    private void toggleEmergency() {
        int red = R.color.regularRed;
        int blue = R.color.regularBlue;
        destination = null;
        ColorStateList toRed = getResources().getColorStateList(red),
                toBlue = getResources().getColorStateList(blue);
        FabAnimation fabAnimation = new FabAnimation();
        ToolbarAnimation toolbarAnimation = new ToolbarAnimation(holder.revealView,
                                                                 holder.revealBackgroundView,
                                                                 holder.toolbar);

        if (!emergency) {
            toolbarAnimation.animateAppAndStatusBar(color(blue), color(red));
            fabAnimation.animateFab(holder.startFabButton, toRed);
            holder.toolbarTitle.setText(R.string.action_emergency);
            holder.destinationViewText.setText(R.string.description_destination_emergency);
            holder.destinationView.setClickable(false);
            emergency = true;
        } else {
            toolbarAnimation.animateAppAndStatusBar(color(red), color(blue));
            fabAnimation.animateFab(holder.startFabButton, toBlue);
            holder.toolbarTitle.setText(R.string.title_activity_navigation);
            holder.destinationViewText.setText(R.string.navigation_select_destination);
            holder.destinationView.setClickable(true);

            holder.destinationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSelectionFragment(v);
                }
            });
            emergency = false;
        }
    }

    /**
     * Responsible for navigation start button
     */
    private class NavigationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (origin == null || destination == null) {
                if (origin == null) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_start_point,
                                   Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_end_point,
                                   Toast.LENGTH_SHORT).show();
                }
            } else {
                if (origin.getId() == destination.getId()) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_different_nodes,
                                   Toast.LENGTH_SHORT).show();
                } else {
                    openNavigatorFragment();
                }
            }
        }
    }

    /**
     * Responsible for downloading maps
     */
    private class MapsDownloaderListener implements TaskListener<Map> {

        @Override
        public void onTaskSuccess(Map map) {
            mapImage = map.getImage();
            holder.mapView.setImage(ImageSource.bitmap(mapImage));
            holder.mapView.setMinimumDpi(40);

            if (origin != null) {
                originText = origin.getName();
                MapPin startPin = new MapPin((float) origin.getX(), (float) origin.getY());
                holder.mapView.setSinglePin(startPin);
            } else {
                originText = getString(R.string.navigation_select_origin);
            }
        }

        @Override
        public void onTaskError(Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_network_download_image),
                           Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskComplete() {
            downloadMapsTask = null;
        }

        @Override
        public void onTaskCancelled() {
            downloadMapsTask = null;
        }
    }

    /**
     * Responsible for downloading nodes
     */
    private class NodesDownloaderListener implements TaskListener<List<Node>> {

        @Override
        public void onTaskSuccess(final List<Node> nodes) {
            List<Node> savedNodes = NodeRepository.findAll();
            if(savedNodes.size() != nodes.size()) {
                NodeRepository.deleteAll();
            }

            Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Node node : nodes) {
                        node.save(databaseWrapper);
                    }
                }
            }).build();

            transaction.execute();

            DownloadEdgesTask task = new DownloadEdgesTask(getContext(), new EdgesDownloaderTaskListener());
            task.execute();
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Download nodes fallito", e);
        }

        @Override
        public void onTaskComplete() {
            /* List<Node> nodes = NodeRepository.findAll();

            for (Node node : nodes) {
                Log.i(TAG, node.toString());
            } */
        }

        @Override
        public void onTaskCancelled() {

        }
    }

    private class EdgesDownloaderTaskListener implements TaskListener<List<Edge>> {
        @Override
        public void onTaskSuccess(final List<Edge> edges) {
            Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                @Override
                public void execute(DatabaseWrapper databaseWrapper) {
                    for (Edge edge : edges) {
                        edge.save(databaseWrapper);
                    }
                }
            }).build();
            transaction.execute();
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Download edges fallito", e);
        }

        @Override
        public void onTaskComplete() {
            /* List<Edge> edges = EdgeRepository.findAll();
            for (Edge edge : edges) {
                Log.i(TAG, edge.toString());
            } */
        }

        @Override
        public void onTaskCancelled() {

        }
    }

    private class MinimumPathListener implements TaskListener<Algorithm.SearchResult> {

        @Override
        public void onTaskSuccess(Algorithm.SearchResult searchResult) {
            Log.i(TAG, searchResult.toString());
            paths = searchResult.getOptimalPaths();
            optimalPath = paths.get(0);
            percorsoOttimoPerPiano = Solution.getSolutionDividedByFloor(optimalPath);
            for (java.util.Map.Entry<String, List<Node>> entry : percorsoOttimoPerPiano.entrySet()) {
                for (Node node : entry.getValue()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + node);
                }
            }

            currentFloor = origin.getFloor();
            holder.mapView.setImage(piantine.get(currentFloor));

            MapPin startPin = new MapPin(origin.toPointF());
            holder.mapView.setSinglePin(startPin);
            holder.mapView.setPath(percorsoOttimoPerPiano.get(origin.getFloor()));
            holder.pathsFabButton.show();

            //Setup listener bottoni piani
            setupFloorButtonListener();
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Errore nel calcolo del percorso minimo", e);
        }

        @Override
        public void onTaskComplete() {
        }

        @Override
        public void onTaskCancelled() {

        }
    }

    /**
     * Imposta i listener sui bottoni dei piani e li nasconde se non contenuti nella soluzione
     */
    private void setupFloorButtonListener() {
        holder.floorButtonContainer.setVisibility(View.VISIBLE);
        Set<String> pianiNellaSoluzione = percorsoOttimoPerPiano.keySet();
        HashMap<String, Button> buttons = getFloorButtons();

        for(String key : buttons.keySet()) {
            buttons.get(key).setVisibility(View.GONE);
        }

        // Soluzione per piano non vuota -> visualizzare il piano
        // Soluzione per piano con un solo punto
        //              -> destinazione => visualizzare il piano
        //              -> != destinazione => nascondere il piano
        // Soluzione per piano vuota -> nascondere il piano

        List<Node> solutionPerFloor;
        boolean onePointSolution, destinationSolution, multiplePointSolution, originSolution;

        for (String floor : pianiNellaSoluzione) {
            solutionPerFloor = percorsoOttimoPerPiano.get(floor);

            onePointSolution = solutionPerFloor.size() == 1;
            multiplePointSolution = solutionPerFloor.size() > 1;
            destinationSolution = (onePointSolution && solutionPerFloor.get(0).equals(destination));
            originSolution = (onePointSolution && solutionPerFloor.get(0).equals(origin));

            if(multiplePointSolution || destinationSolution || originSolution) {
                buttons.get(floor).setVisibility(View.VISIBLE);
                buttons.get(floor).setOnClickListener(new FloorButtonListener());
            }
        }
    }

    /**
     * Ritorna la lista dei bottoni dei piani
     *
     * @return
     */
    private HashMap<String, Button> getFloorButtons() {
        ViewGroup buttonContainer = holder.floorButtonContainer;
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

    private class FloorButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String floor = button.getText().toString();

            if(!floor.equals(currentFloor)) {
                currentFloor = floor;

                Bitmap map = piantine.get(floor);
                Bitmap mapCopy = map.copy(map.getConfig(), true);

                holder.mapView.setImage(mapCopy);

                MapPin originPin = new MapPin(origin.toPointF());
                MapPin destinationPin = new MapPin(destination.toPointF());


                // @TODO Disegnare entrambi i pin quando sono sullo stesso piano
                if (floor.equals(origin.getFloor())) {
                    holder.mapView.setSinglePin(originPin, PinView.Colors.RED);
                } else if (floor.equals(destination.getFloor())) {
                    holder.mapView.setSinglePin(destinationPin, PinView.Colors.BLUE);
                } else {
                    holder.mapView.resetPins();
                }

                holder.mapView.setPath(percorsoOttimoPerPiano.get(floor));
            }
        }
    }

    /**
     * Listener per gestire la selezione di un percorso diverso
     */
    private class PathButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = getContext();
            ArrayList<String> options = new ArrayList<>(paths.size());
            for (int i = 0; i < paths.size(); i++) {
                options.add("Percorso " + String.valueOf(i + 1));
            }

            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title(context.getString(R.string.select_path))
                    .items(options)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/
                            return true;
                        }
                    })
                    .widgetColorRes(R.color.regularBlue)
                    .positiveText(R.string.action_confirm)
                    .positiveColorRes(R.color.darkBlue)
                    .negativeText(R.string.action_back)
                    .negativeColorRes(R.color.black)
                    .show();
        }
    }

    /**
     * Classe wrapper degli elementi della vista
     */
    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final FloatingActionButton startFabButton;
        public final FloatingActionButton pathsFabButton;
        public final View revealView;
        public final View revealBackgroundView;
        public final View destinationView;
        public final View originView;
        public final TextView destinationViewText;
        public final TextView originViewText;
        public final ImageView originViewIcon;
        public final TextView destinationViewPlaceholder;
        public final ImageView destinationViewIcon;
        public final TextView originViewPlaceholder;
        public final PinView mapView;
        public final ViewGroup floorButtonContainer;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.navigation_toolbar_textview_title);
            startFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_start);
            pathsFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_paths);
            revealView = view.findViewById(R.id.reveal_view);
            revealBackgroundView = view.findViewById(R.id.reveal_background_view);
            mapView = (PinView) view.findViewById(R.id.navigation_map_image);
            floorButtonContainer = (ViewGroup) view.findViewById(R.id.floor_button_container);

            destinationView = view.findViewById(R.id.navigation_input_destination);
            destinationViewText = (TextView) destinationView.findViewById(R.id.text);
            destinationViewPlaceholder = (TextView) destinationView.findViewById(R.id.placeholder);
            destinationViewIcon = (ImageView) destinationView.findViewById(R.id.icon);
            originView = view.findViewById(R.id.navigation_input_origin);
            originViewText = (TextView) originView.findViewById(R.id.text);
            originViewPlaceholder = (TextView) originView.findViewById(R.id.placeholder);
            originViewIcon = (ImageView) originView.findViewById(R.id.icon);
        }
    }
}
