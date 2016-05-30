package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.listeners.EdgesDownloaderTaskListener;
import com.dii.ids.application.main.navigation.listeners.NodesDownloaderTaskListener;
import com.dii.ids.application.main.navigation.tasks.EdgesDownloaderTask;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;
import com.dii.ids.application.main.navigation.tasks.MinimumPathTask;
import com.dii.ids.application.main.navigation.tasks.NearestExitTask;
import com.dii.ids.application.main.navigation.tasks.NodesDownloaderTask;
import com.dii.ids.application.navigation.MultiFloorPath;
import com.dii.ids.application.navigation.Path;
import com.dii.ids.application.views.MapView;
import com.dii.ids.application.views.exceptions.DestinationNotSettedException;
import com.dii.ids.application.views.exceptions.OriginNotSettedException;
import com.dii.ids.application.views.exceptions.PiantineNotSettedException;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * HomeFragment: classe per la schermata principale nel contesto di navigazione.
 */
public class HomeFragment extends BaseFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String INTENT_KEY_POSITION = "position";
    private static String originText;
    private static String destinationText;
    private static Node origin = null, destination = null, emergencyDestination = null;
    private ViewHolder holder;
    private List<Path> solutionPaths = null;
    private Path selectedSolution;
    private HashMap<Integer, MapsDownloaderTask> downloadMapsTasks;
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
                    holder.mapView.setOrigin(origin);
                    break;
                case DESTINATION_SELECTION_REQUEST_CODE:
                    destination = node;
                    holder.mapView.setDestination(destination);
                    break;
            }
        } catch (NullPointerException ee) {
            Log.e(TAG, "NullPointer", ee);
        } catch (PiantineNotSettedException e) {
            e.printStackTrace();
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

        NodesDownloaderTask nodesDownloaderTask = new NodesDownloaderTask(
                getContext(), new NodesDownloaderTaskListener());
        nodesDownloaderTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        EdgesDownloaderTask task = new EdgesDownloaderTask(
                getContext(), new EdgesDownloaderTaskListener());
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        return view;
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
                holder.setupMapView();
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
        Node dest = emergency ? emergencyDestination : destination;

        NavigatorFragment navigatorFragment =
                NavigatorFragment.newInstance(origin, dest, selectedSolution);
        ((NavigationActivity) getActivity())
                .changeFragment(navigatorFragment);
    }


    /**
     * Responsible for navigation start button
     */
    private class NavigationButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (!emergency) {
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
            } else {
                if (origin == null) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_start_point,
                                   Toast.LENGTH_SHORT).show();
                } else {
                    openNavigatorFragment();
                }
            }
        }
    }

    // @TODO Esternalizzare
    private class MinimumPathListener implements TaskListener<List<Path>> {
        @Override
        public void onTaskSuccess(List<Path> searchResult) {
            solutionPaths = searchResult;
            selectedSolution = new Path(solutionPaths.get(0));
            MultiFloorPath multiFloorSolution = selectedSolution.toMultiFloorPath();

            try {
                holder.mapView.setOrigin(origin)
                        .setDestination(destination)
                        .drawRoute(multiFloorSolution);
            } catch (PiantineNotSettedException | OriginNotSettedException | DestinationNotSettedException e) {
                e.printStackTrace();
            }

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
            ArrayList<String> options = new ArrayList<>(solutionPaths.size());
            for (int i = 0; i < solutionPaths.size(); i++) {
                options.add(getString(R.string.label_select_path, i + 1));
            }

            new MaterialDialog.Builder(getContext())
                    .title(getContext().getString(R.string.select_path))
                    .items(options)
                    .itemsCallbackSingleChoice(indexOfPathSelected, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            indexOfPathSelected = which;
                            selectedSolution = new Path(solutionPaths.get(indexOfPathSelected));
                            MultiFloorPath multiFloorPath = solutionPaths.get(which).toMultiFloorPath();
                            try {
                                holder.mapView.drawRoute(multiFloorPath);
                            } catch (PiantineNotSettedException | OriginNotSettedException | DestinationNotSettedException e) {
                                e.printStackTrace();
                            }
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

    private class NearestExitListener implements TaskListener<List<Path>> {

        @Override
        public void onTaskSuccess(List<Path> exitPaths) {
            Log.i(TAG, exitPaths.toString());
            solutionPaths = exitPaths;
            selectedSolution = new Path(solutionPaths.get(0));
            emergencyDestination = (Node) selectedSolution.get(selectedSolution.size() - 1);
            MultiFloorPath multiFloorSolution = selectedSolution.toMultiFloorPath();

            try {
                holder.mapView.setOrigin(origin)
                        .setDestination(emergencyDestination)
                        .drawRoute(multiFloorSolution);
            } catch (PiantineNotSettedException | OriginNotSettedException | DestinationNotSettedException e) {
                e.printStackTrace();
            }

            holder.pathsFabButton.show();
        }

        @Override
        public void onTaskError(Exception e) {
            Log.e(TAG, "Error searching exit", e);
        }

        @Override
        public void onTaskComplete() {
        }

        @Override
        public void onTaskCancelled() {
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

            if (solutionPaths == null) {
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

          setupMapView();
        }

        public void setupMapView() {
            if (!emergency) {
                if(destination != null && origin == null) {
                    try {
                        holder.mapView.setDestination(destination);
                    } catch (PiantineNotSettedException e) {
                        e.printStackTrace();
                    }
                } else if(origin != null && destination == null) {
                    try {
                        holder.mapView.setOrigin(origin);
                    } catch (PiantineNotSettedException e) {
                        e.printStackTrace();
                    }
                } else if(origin != null && destination != null) {
                    MinimumPathTask minimumPathTask = new MinimumPathTask(
                            getContext(), new MinimumPathListener());
                    minimumPathTask.execute(origin, destination);
                }
            } else {
                if(origin != null) {
                    NearestExitTask nearestExitTask = new NearestExitTask(
                            getContext(), new NearestExitListener());
                    nearestExitTask.execute(origin);
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
