package com.dii.ids.application.main.navigation;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dii.ids.application.R;
import com.dii.ids.application.animations.FabAnimation;
import com.dii.ids.application.animations.ToolbarAnimation;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends MapFragment {

    private ViewHolder holder;
    private boolean emergency = false;
    private MapsDownloaderTask mapsDownloaderTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        holder = new ViewHolder(view);

        // Setup textview text
        holder.destinationViewPlaceholder.setText(R.string.navigation_going_to);
        holder.originViewText.setText(R.string.navigation_select_origin);
        holder.destinationViewText.setText(R.string.navigation_select_destination);


        // Setup listeners
        holder.originView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectionFragment(getString(R.string.navigation_select_origin));
            }
        });

        holder.destinationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectionFragment(getString(R.string.navigation_select_destination));
            }
        });

        holder.startFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mapsDownloaderTask = new MapsDownloaderTask()
                .inject(this);
        int floors[] = {145, 150, 155};
        int idx = new Random().nextInt(floors.length);
        mapsDownloaderTask.execute(floors[idx]);

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
                toogleEmergency();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update view for normal/emergency state
     */
    private void toogleEmergency() {
        int red = R.color.regularRed;
        int blue = R.color.regularBlue;
        FabAnimation fabAnimation = new FabAnimation(this);
        ToolbarAnimation toolbarAnimation = new ToolbarAnimation(this,
                holder.revealView,
                holder.revealBackgroundView,
                holder.toolbar);

        if (!emergency) {
            toolbarAnimation.animateAppAndStatusBar(blue, red);
            fabAnimation.animateFab(holder.startFabButton, red);
            holder.toolbarTitle.setText(R.string.action_emergency);
            holder.destinationViewText.setText(R.string.description_destination_emergency);
            holder.destinationView.setClickable(false);
            emergency = true;
        } else {
            toolbarAnimation.animateAppAndStatusBar(red, blue);
            fabAnimation.animateFab(holder.startFabButton, blue);
            holder.toolbarTitle.setText(R.string.title_activity_navigation);
            holder.destinationViewText.setText(R.string.navigation_select_destination);
            holder.destinationView.setClickable(true);
            emergency = false;
        }
    }

    private void openSelectionFragment(String message) {
        SelectionFragment selectionFragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        selectionFragment = SelectionFragment.newInstance(message);

        fragmentTransaction.replace(R.id.navigation_content_pane, selectionFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTaskSuccess(MapsDownloaderTask asyncTask) {
        final Bitmap image = mapsDownloaderTask.getImage();
        this.mapsDownloaderTask = null;

        holder.mapImage.setImage(ImageSource.bitmap(image));
        holder.mapImage.setMinimumDpi(40);

        // @TODO Spostare il gestore della gesture nel fragment di competenza
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(holder.mapImage.isReady()) {
                    PointF sCoord = holder.mapImage.viewToSourceCoord(e.getX(), e.getY());
                    Toast.makeText(getActivity().getApplicationContext(), "Tap on [" +
                        Double.toString(sCoord.x) + "," + Double.toString(sCoord.y), Toast.LENGTH_SHORT).show();
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
    public void onTaskError(MapsDownloaderTask asyncTask) {
        this.mapsDownloaderTask = null;
        Toast.makeText(getContext(), getString(R.string.error_network_download_image), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCancelled(MapsDownloaderTask asyncTask) {
        this.mapsDownloaderTask = null;
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
        public final TextView destinationViewPlaceholder;
        public final TextView originViewPlaceholder;
        public final SubsamplingScaleImageView mapImage;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.navigation_toolbar_textview_title);
            startFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_start);
            revealView = view.findViewById(R.id.reveal_view);
            revealBackgroundView = view.findViewById(R.id.reveal_background_view);
            mapImage = (SubsamplingScaleImageView) view.findViewById(R.id.navigation_map_image);

            destinationView = view.findViewById(R.id.navigation_input_destination);
            destinationViewText = (TextView) destinationView.findViewById(R.id.text);
            destinationViewPlaceholder = (TextView) destinationView.findViewById(R.id.placeholder);
            originView = view.findViewById(R.id.navigation_input_origin);
            originViewText = (TextView) originView.findViewById(R.id.text);
            originViewPlaceholder = (TextView) originView.findViewById(R.id.placeholder);
        }
    }
}
