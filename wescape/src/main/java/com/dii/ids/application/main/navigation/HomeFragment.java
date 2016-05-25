package com.dii.ids.application.main.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dii.ids.application.R;
import com.dii.ids.application.animations.FabAnimation;
import com.dii.ids.application.animations.ToolbarAnimation;
import com.dii.ids.application.entity.Map;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.listeners.EdgesDownloaderTaskListener;
import com.dii.ids.application.main.navigation.listeners.NodesDownloaderTaskListener;
import com.dii.ids.application.main.navigation.tasks.EdgesDownloaderTask;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;
import com.dii.ids.application.main.navigation.tasks.MinimumPathTask;
import com.dii.ids.application.main.navigation.tasks.NodesDownloaderTask;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.views.MapView;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.usc.citius.hipster.algorithm.Algorithm;

/**
 * HomeFragment: classe per la schermata principale nel contesto di navigazione.
 */
public class HomeFragment extends BaseFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String INTENT_KEY_POSITION = "position";
    private static String originText;
    private static String destinationText;
    private static Node origin = null, destination = null;
    private ViewHolder holder;
    private List<Path> paths = null;
    private HashMap<Integer, MapsDownloaderTask> downloadMapsTasks;
    private HashMap<String, Bitmap> piantine;
    private Path minPathSolution;
    private int indexOfPathSelected;
    private boolean emergency = false;


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
                    Log.i(TAG, node.toString());
                    holder.mapView.setOrigin(origin);
                    break;
                case DESTINATION_SELECTION_REQUEST_CODE:
                    destination = node;
                    holder.mapView.setDestination(destination);
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

        holder.setupUI();
        downloadMaps();

        NodesDownloaderTask nodesDownloaderTask = new NodesDownloaderTask(
                getContext(), new NodesDownloaderTaskListener());
        nodesDownloaderTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        EdgesDownloaderTask task = new EdgesDownloaderTask(
                getContext(), new EdgesDownloaderTaskListener());
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        return view;
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

        downloadMapsTasks = new HashMap<>();
        for (int piano : piani) {
            downloadMapsTasks.put(piano, new MapsDownloaderTask(getContext(), new MapListener()));
            downloadMapsTasks.get(piano).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, piano);
        }
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
                holder.toggleEmergency();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openSelectionFragment(View v) {
        SelectionFragment selectionFragment;

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
        ((NavigationActivity) getActivity()).changeFragment(selectionFragment);
    }

    private void openNavigatorFragment() {
        NavigatorFragment navigatorFragment = NavigatorFragment.newInstance(origin, destination, piantine, minPathSolution);
        ((NavigationActivity) getActivity())
                .changeFragment(navigatorFragment);
    }

    /**
     * Listner che riempie la hasmap delle piantine
     */
    // @TODO Esternalizzare
    private class MapListener implements TaskListener<Map> {
        @Override
        public void onTaskSuccess(Map map) {
            Log.i("Piantina", map.getImage().toString());
            piantine.put(map.getFloor(), map.getImage());
            Log.i("Piantina", String.valueOf(piantine.size()));
            downloadMapsTasks.remove(map.getFloorInt());
        }

        @Override
        public void onTaskError(Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_network_download_image),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskComplete() {
            if (downloadMapsTasks.isEmpty()) {
                Log.i(TAG, "Imposto piantine");
                holder.mapView.setPiantine(piantine);
            }
        }

        @Override
        public void onTaskCancelled() {

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

    // @TODO Esternalizzare
    private class MinimumPathListener implements TaskListener<Algorithm.SearchResult> {
        @Override
        public void onTaskSuccess(Algorithm.SearchResult searchResult) {
            Log.i(TAG, searchResult.toString());
            paths = searchResult.getOptimalPaths();
            indexOfPathSelected = 0;
            minPathSolution = new Path(paths.get(indexOfPathSelected));

            holder.mapView.setOrigin(origin);
            holder.mapView.setDestination(destination);

            for (String floor : piantine.keySet()) {
                Log.i(TAG, floor + " " + piantine.get(floor).toString());
            }
            holder.mapView.setPiantine(piantine);
            Log.i(TAG, "Percorso minimo!");
            MultiFloorPath multiFloorSolution = minPathSolution.toMultiFloorPath();
            holder.mapView.setMultiFloorPath(multiFloorSolution);
            holder.pathsFabButton.show();
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
                    .itemsCallbackSingleChoice(indexOfPathSelected, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            //TODO: decommentare quando verranno ritornati piu percorsi
//                            indexOfPathSelected = which;
//                            HashMap<String, List<Node>> path = Solution.getSolutionDividedByFloor(paths.get(which));
//                            holder.mapView.setMultiFloorPath(path);
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
    public class ViewHolder extends BaseFragment.ViewHolder {
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
        public final MapView mapView;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.navigation_toolbar_textview_title);
            startFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_start);
            pathsFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_paths);
            revealView = view.findViewById(R.id.reveal_view);
            revealBackgroundView = view.findViewById(R.id.reveal_background_view);
            mapView = find(view, R.id.navigation_map);

            destinationView = view.findViewById(R.id.navigation_input_destination);
            destinationViewText = (TextView) destinationView.findViewById(R.id.text);
            destinationViewPlaceholder = (TextView) destinationView.findViewById(R.id.placeholder);
            destinationViewIcon = (ImageView) destinationView.findViewById(R.id.icon);
            originView = view.findViewById(R.id.navigation_input_origin);
            originViewText = (TextView) originView.findViewById(R.id.text);
            originViewPlaceholder = (TextView) originView.findViewById(R.id.placeholder);
            originViewIcon = (ImageView) originView.findViewById(R.id.icon);
        }

        /**
         * Setup dell'interfaccia
         */
        private void setupUI() {
            originViewPlaceholder.setText(R.string.navigation_starting_from);
            originViewIcon.setImageResource(R.drawable.ic_my_location);
            destinationViewPlaceholder.setText(R.string.navigation_going_to);
            destinationViewIcon.setImageResource(R.drawable.ic_pin_drop);

            originText = origin == null ?
                    getString(R.string.navigation_select_origin) :
                    origin.getName();
            destinationText = destination == null ?
                    getString(R.string.navigation_select_destination) :
                    destination.getName();

            originViewText.setText(originText);
            destinationViewText.setText(destinationText);

            // Setup listeners
            originView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSelectionFragment(v);
                }
            });

            startFabButton.setOnClickListener(new NavigationButtonListener());

            if (paths == null) {
                pathsFabButton.hide();
            }

            pathsFabButton.setOnClickListener(new PathButtonListener());

            if (emergency) {
                revealView.setBackgroundColor(color(R.color.regularRed));
                revealBackgroundView.setBackgroundColor(color(R.color.regularRed));
                startFabButton.setBackgroundTintList(ColorStateList.valueOf(color(R.color.regularRed)));
                toolbarTitle.setText(R.string.action_emergency);
                destinationViewText.setText(R.string.description_destination_emergency);
                destinationView.setClickable(false);

            } else {
                destinationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSelectionFragment(v);
                    }
                });
                destinationView.setClickable(true);
            }

            if (origin != null) {
                if (destination != null) {
                    MinimumPathTask minimumPathTask = new MinimumPathTask(getContext(), new MinimumPathListener());
                    minimumPathTask.execute(origin, destination);
                } else {
                    holder.mapView.setOrigin(origin);
                    holder.mapView.changeFloor(origin.getFloor());
                }
            }
        }

        /**
         * Update view for normal/emergency state
         */
        public void toggleEmergency() {
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
    }
}
