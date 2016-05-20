package com.dii.ids.application.main.navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.dii.ids.application.R;
import com.dii.ids.application.entity.Node;
import com.dii.ids.application.entity.Position;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.DownloadMapsTask;
import com.dii.ids.application.main.navigation.tasks.SelectablePointsTask;
import com.dii.ids.application.main.navigation.views.MapPin;
import com.dii.ids.application.main.navigation.views.PinView;
import com.dii.ids.application.utils.units.UnitConverter;

import org.apache.commons.lang3.SerializationUtils;

public class SelectionFromMapFragment extends BaseFragment {
    public static final int POSITION_ACQUIRED = 1;
    public static final int POSITION_NOT_ACQUIRED = 0;
    public static final String TAG = SelectionFromMapFragment.class.getSimpleName();
    private static final int SEARCH_RADIUS_IN_DP = 100;
    private DownloadMapsTask mapsTask;
    private ViewHolder holder;
    private int currentFloor = STARTING_FLOOR;
    private Node selectedNode = null;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static SelectionFromMapFragment newInstance() {
        SelectionFromMapFragment fragment = new SelectionFromMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void toggleConfirmButtonState() {
        if (selectedNode == null) {
            disableConfirmButtonState();
        } else {
            holder.confirmButton.setEnabled(true);
            holder.confirmButton.setTextColor(color(R.color.linkText));
        }
    }

    private void disableConfirmButtonState() {
        holder.confirmButton.setEnabled(false);
        holder.confirmButton.setTextColor(color(R.color.disabledText));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_selection_from_map_fragment, container, false);
        holder = new ViewHolder(view);

        switch (getTargetRequestCode()) {
            case ORIGIN_SELECTION_REQUEST_CODE:
                holder.toolbarTitle.setText(R.string.navigation_select_origin);
                break;
            case DESTINATION_SELECTION_REQUEST_CODE:
                holder.toolbarTitle.setText(R.string.navigation_select_destination);
                break;
        }

        toggleConfirmButtonState();

        mapsTask = new DownloadMapsTask(getContext(), new MapsDownloaderListener());
        mapsTask.execute(STARTING_FLOOR);
        holder.floor155Button.setTextColor(color(R.color.linkText));

        holder.floor155Button.setOnClickListener(new FloorButtonListener());
        holder.floor150Button.setOnClickListener(new FloorButtonListener());
        holder.floor145Button.setOnClickListener(new FloorButtonListener());

        // Setup back button
        holder.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositionConfirm(selectedNode);
            }
        });

        return view;
    }

    public void onPositionConfirm(Node node) {
        Intent data = new Intent();
        data.putExtra(HomeFragment.INTENT_KEY_POSITION, SerializationUtils.serialize(node));
        getTargetFragment().onActivityResult(getTargetRequestCode(), POSITION_ACQUIRED, data);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
        fm.popBackStack();
    }

    private class FloorButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            holder.floor155Button.setTextColor(color(R.color.black));
            holder.floor150Button.setTextColor(color(R.color.black));
            holder.floor145Button.setTextColor(color(R.color.black));
            button.setTextColor(color(R.color.linkText));
            int floor = Integer.parseInt(button.getText().toString());

            currentFloor = floor;

            if (mapsTask == null) {
                mapsTask = new DownloadMapsTask(getContext(), new MapsDownloaderListener());
                mapsTask.execute(floor);
            }
        }
    }

    private class MapsDownloaderListener implements TaskListener<Bitmap> {

        @Override
        public void onTaskSuccess(Bitmap image) {
            holder.mapView.setImage(ImageSource.bitmap(image));
            holder.mapView.setMinimumDpi(40);
            holder.mapView.resetPins();
            disableConfirmButtonState();

            final GestureDetector gestureDetector = new GestureDetector(getActivity(), new MapGestureDetector());

            holder.mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

        private class MapGestureDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (holder.mapView.isReady()) {
                    PointF tappedCoordinates = holder.mapView.viewToSourceCoord(e.getX(), e.getY());
                    Position tappedPosition = new Position(tappedCoordinates.x, tappedCoordinates.y,
                                                           Integer.toString(currentFloor));

                    SelectablePointsTask selectablePointsTask = new SelectablePointsTask(
                            new SelectablePointsListener(),
                            (int) UnitConverter.convertDpToPixel(SEARCH_RADIUS_IN_DP, getContext()));
                    selectablePointsTask.execute(tappedPosition);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Image is not ready",
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }

        private class SelectablePointsListener implements TaskListener<Node> {

            @Override
            public void onTaskSuccess(Node node) {
                selectedNode = node;
                holder.selectedNode.setText(node.getName());
                holder.mapView.setSinglePin(new MapPin((float) node.getX(), (float) node.getY(), 0));
                toggleConfirmButtonState();

                Toast.makeText(getActivity().getApplicationContext(), "Hai selezionato il nodo: " + node.getName(),
                               Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTaskError(Exception e) {
                if (e == null) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.no_node_in_range,
                                   Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Errore di selezione", e);
                }
            }

            @Override
            public void onTaskComplete() {

            }

            @Override
            public void onTaskCancelled() {

            }
        }

        @Override
        public void onTaskError(Exception e) {
            Toast.makeText(getContext(), getString(R.string.error_network_download_image), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskComplete() {
            mapsTask = null;
        }

        @Override
        public void onTaskCancelled() {
            mapsTask = null;
        }
    }

    public class ViewHolder extends BaseFragment.ViewHolder {
        public final PinView mapView;
        public final Button floor155Button;
        public final Button floor150Button;
        public final Button floor145Button;
        public final View actionButtonsContainer;
        public final Button backButton;
        public final Button confirmButton;
        public final TextView toolbarTitle;
        public final TextView selectedNode;

        public ViewHolder(View v) {
            mapView = find(v, R.id.navigation_map_image);
            floor155Button = find(v, R.id.floor_button_155);
            floor150Button = find(v, R.id.floor_button_150);
            floor145Button = find(v, R.id.floor_button_145);
            actionButtonsContainer = find(v, R.id.action_buttons_container);
            backButton = find(v, R.id.back_button);
            confirmButton = find(v, R.id.confirm_button);
            toolbarTitle = find(v, R.id.toolbar_title);
            selectedNode = find(v, R.id.node);
        }
    }
}
