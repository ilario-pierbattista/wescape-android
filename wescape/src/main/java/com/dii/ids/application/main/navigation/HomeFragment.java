package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dii.ids.application.R;
import com.dii.ids.application.animations.FabAnimation;
import com.dii.ids.application.animations.ToolbarAnimation;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.repository.NodeRepository;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.DownloadNodesTask;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;
import com.dii.ids.application.main.navigation.views.MapPin;
import com.dii.ids.application.main.navigation.views.PinView;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private boolean emergency = false;
    private MapsDownloaderTask mapsDownloaderTask;
    private DownloadNodesTask downloadNodesTask;

    private TaskListener<Bitmap> mapDownloaderListener =
            new TaskListener<Bitmap>() {
                @Override
                public void onTaskSuccess(Bitmap image) {
                    holder.mapImage.setImage(ImageSource.bitmap(image));
                    holder.mapImage.setMinimumDpi(40);

                    // @TODO Porchetto a tutto volume
                    MapPin mapPin = new MapPin(500f, 900f, 1);
                    MapPin mapPin1 = new MapPin(550f, 1000f, 2);

                    final ArrayList<MapPin> MapPins = new ArrayList();
                    MapPins.add(mapPin);
                    MapPins.add(mapPin1);

                    ArrayList<PointF> points = new ArrayList<>(
                            Arrays.asList(
                                    new PointF(400f, 500f),
                                    new PointF(800f, 500f),
                                    new PointF(300f, 200f),
                                    new PointF(100f, 900f),
                                    new PointF(300f, 700f)));

                    holder.mapImage.setPath(points);

                    holder.mapImage.setMultiplePins(MapPins);

                    // @TODO Spostare il gestore della gesture nel fragment di competenza
                    final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            if (holder.mapImage.isReady()) {
                                PointF sCoord = holder.mapImage.viewToSourceCoord(e.getX(), e.getY());
                                //Toast.makeText(getActivity().getApplicationContext(), "Tap on [" +
                                //      Double.toString(sCoord.x) + "," + Double.toString(sCoord.y), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Image is not ready", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    holder.mapImage.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return gestureDetector.onTouchEvent(event);
                        }
                    });
                }

                @Override
                public void onTaskError(Exception e) {
                    Toast.makeText(getContext(), getString(R.string.error_network_download_image), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTaskComplete() {
                    mapsDownloaderTask = null;
                }

                @Override
                public void onTaskCancelled() {
                    mapsDownloaderTask = null;
                }
            };

    private TaskListener<List<Node>> nodesDownloaderListener =
            new TaskListener<List<Node>>() {
                @Override
                public void onTaskSuccess(final List<Node> nodes) {

                    Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            for (Node node : nodes) {
                                node.save(databaseWrapper);
                            }
                        }
                    }).build();

                    transaction.execute();
                }

                @Override
                public void onTaskError(Exception e) {
                    Log.i(TAG, "Download fallito");
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onTaskComplete() {
                    List<Node> nodes = NodeRepository.findAll();

                    for (Node node : nodes) {
                        Log.i(TAG, node.toString());
                    }
                }

                @Override
                public void onTaskCancelled() {

                }
            };

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

        downloadNodesTask = new DownloadNodesTask(getContext(), nodesDownloaderListener);
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

        holder.startFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNavigatorFragment();
            }
        });

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

        // @TODO Sostituire con qualcosa di meno insensato
        mapsDownloaderTask = new MapsDownloaderTask(getContext(), mapDownloaderListener);
        int floors[] = {145, 150, 155};
        int idx = new Random().nextInt(floors.length);
        mapsDownloaderTask.execute(floors[idx]);
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
     * Classe wrapper degli elementi della vista
     */
    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final FloatingActionButton startFabButton;
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
        public final PinView mapImage;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.navigation_toolbar_textview_title);
            startFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_start);
            revealView = view.findViewById(R.id.reveal_view);
            revealBackgroundView = view.findViewById(R.id.reveal_background_view);
            mapImage = (PinView) view.findViewById(R.id.navigation_map_image);

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
