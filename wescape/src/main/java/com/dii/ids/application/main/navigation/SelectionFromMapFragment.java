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
import com.dii.ids.application.entity.Position;
import com.dii.ids.application.listener.TaskListener;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.DownloadMapsTask;
import com.dii.ids.application.main.navigation.views.MapPin;
import com.dii.ids.application.main.navigation.views.PinView;

import org.apache.commons.lang3.SerializationUtils;

public class SelectionFromMapFragment extends BaseFragment {
    public static final String FRAGMENT_TAG = SelectionFromMapFragment.class.getSimpleName();
    public static final int STARTING_FLOOR = 155;
    public static final int POSITION_ACQUIRED = 1;
    public static final int POSITION_NOT_ACQUIRED = 0;
    private static final String LOG_TAG = SelectionFromMapFragment.class.getSimpleName();
    private DownloadMapsTask mapsTask;
    private ViewHolder holder;
    private PointF tappedCoordinates;
    private int displayedFloor = STARTING_FLOOR;
    private int selectedFloor;

    private TaskListener<Bitmap> taskListener =
            new TaskListener<Bitmap>() {
                @Override
                public void onTaskSuccess(Bitmap image) {
                    holder.mapView.setImage(ImageSource.bitmap(image));
                    holder.mapView.setMinimumDpi(40);
                    holder.mapView.resetPins();
                    disableConfirmButtonState();

                    final GestureDetector gestureDetector
                            = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            if (holder.mapView.isReady()) {
                                tappedCoordinates = holder.mapView.viewToSourceCoord(e.getX(), e.getY());

                                // @TODO Trasformare le coordinate per azzeccare il nodo
                                // @TODO Valutare l'uso dell'id numerico
                                // mapPins.add(new MapPin(tappedCoordinates.x, tappedCoordinates.y, 0));
                                // holder.mapView.setMultiplePins(mapPins);
                                holder.mapView.setSinglePin(new MapPin(tappedCoordinates.x, tappedCoordinates.y, 0));
                                Log.i(TAG, "Clicked: " + tappedCoordinates.x + " " + tappedCoordinates.y);

                                selectedFloor = displayedFloor;

                                toogleConfirmButtonState();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Image is not ready", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    holder.mapView.setOnTouchListener(new View.OnTouchListener() {
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
                    mapsTask = null;
                }

                @Override
                public void onTaskCancelled() {
                    mapsTask = null;
                }
            };

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static SelectionFromMapFragment newInstance() {
        SelectionFromMapFragment fragment = new SelectionFromMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void toogleConfirmButtonState() {
        if (tappedCoordinates == null) {
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

        toogleConfirmButtonState();

        mapsTask = new DownloadMapsTask(getContext(), taskListener);
        mapsTask.execute(STARTING_FLOOR);
        holder.floor155Button.setTextColor(color(R.color.linkText));

        // @TODO trovare una soluzione più elegante per questi listeners
        holder.floor155Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorButtonListener(v);
            }
        });
        holder.floor150Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorButtonListener(v);
            }
        });
        holder.floor145Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorButtonListener(v);
            }
        });

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
                onPositionConfirm(tappedCoordinates, selectedFloor);
            }
        });

        return view;
    }

    public void floorButtonListener(View v) {
        Button button = (Button) v;
        holder.floor155Button.setTextColor(color(R.color.black));
        holder.floor150Button.setTextColor(color(R.color.black));
        holder.floor145Button.setTextColor(color(R.color.black));
        button.setTextColor(color(R.color.linkText));
        int floor = Integer.parseInt(button.getText().toString());
        displayedFloor = floor;
        if (mapsTask == null) {
            mapsTask = new DownloadMapsTask(getContext(), taskListener);
            mapsTask.execute(floor);
        }
    }

    public void onPositionConfirm(PointF coordinates, int floor) {
        Intent data = new Intent();
        Position position = new Position(coordinates.x, coordinates.y, floor);
        data.putExtra(HomeFragment.INTENT_KEY_POSITION, SerializationUtils.serialize(position));
        getTargetFragment().onActivityResult(getTargetRequestCode(), POSITION_ACQUIRED, data);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
        fm.popBackStack();
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

        public ViewHolder(View v) {
            mapView = find(v, R.id.navigation_map_image);
            floor155Button = find(v, R.id.floor_button_155);
            floor150Button = find(v, R.id.floor_button_150);
            floor145Button = find(v, R.id.floor_button_145);
            actionButtonsContainer = find(v, R.id.action_buttons_container);
            backButton = find(v, R.id.back_button);
            confirmButton = find(v, R.id.confirm_button);
            toolbarTitle = find(v, R.id.toolbar_title);
        }
    }
}
