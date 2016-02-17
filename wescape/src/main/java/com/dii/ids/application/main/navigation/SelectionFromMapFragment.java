package com.dii.ids.application.main.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dii.ids.application.R;
import com.dii.ids.application.interfaces.OnPositionSelectedListener;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

public class SelectionFromMapFragment extends MapFragment {
    public static final int STARTING_FLOOR = 155;
    private static final String LOG_TAG = SelectionFromMapFragment.class.getSimpleName();
    int blue, black, disabled;
    private MapsDownloaderTask mapsTask;
    private ViewHolder holder;
    private OnPositionSelectedListener callBack;
    private PointF mCoordinates;

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param selection Parameter 1.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    public static SelectionFromMapFragment newInstance(String selection) {
        SelectionFromMapFragment fragment = new SelectionFromMapFragment();
        Bundle args = new Bundle();
        args.putString(TOOLBAR_TITLE, selection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onTaskSuccess(MapsDownloaderTask asyncTask) {
        final Bitmap image = mapsTask.getImage();
        this.mapsTask = null;

        holder.mapView.setImage(ImageSource.bitmap(image));
        holder.mapView.setMinimumDpi(40);

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (holder.mapView.isReady()) {
                    mCoordinates = holder.mapView.viewToSourceCoord(e.getX(), e.getY());
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
    public void onTaskError(MapsDownloaderTask asyncTask) {
        this.mapsTask = null;
        Toast.makeText(getContext(), getString(R.string.error_network_download_image), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCancelled(MapsDownloaderTask asyncTask) {
        this.mapsTask = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callBack = (OnPositionSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_selection_from_map_fragment, container, false);
        black = getResources().getColor(R.color.black);
        blue = getResources().getColor(R.color.linkText);
        disabled = getResources().getColor(R.color.disabledText);
        holder = new ViewHolder(view);

        toogleConfirmButtonState();

        mapsTask = new MapsDownloaderTask().inject(this);
        mapsTask.execute(STARTING_FLOOR);
        holder.floor155Button.setTextColor(blue);

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
                callBack.onPositionConfirm(mCoordinates);
            }
        });

        return view;
    }

    private void toogleConfirmButtonState() {
        if (mCoordinates == null) {
            holder.confirmButton.setEnabled(false);
            holder.confirmButton.setTextColor(disabled);
        } else {
            holder.confirmButton.setEnabled(true);
            holder.confirmButton.setTextColor(blue);
        }
    }

    public void floorButtonListener(View v) {
        Button button = (Button) v;
        holder.floor155Button.setTextColor(black);
        holder.floor150Button.setTextColor(black);
        holder.floor145Button.setTextColor(black);
        button.setTextColor(blue);
        int floor = Integer.parseInt(button.getText().toString());
        if (mapsTask == null) {
            mapsTask = new MapsDownloaderTask()
                    .inject(this);
            mapsTask.execute(floor);
        }
    }

    public class ViewHolder {
        public final SubsamplingScaleImageView mapView;
        public final Button floor155Button;
        public final Button floor150Button;
        public final Button floor145Button;
        public final View actionButtonsContainer;
        public final Button backButton;
        public final Button confirmButton;

        public ViewHolder(View v) {
            mapView = (SubsamplingScaleImageView) v.findViewById(R.id.navigation_map_image);
            floor155Button = (Button) v.findViewById(R.id.floor_button_155);
            floor150Button = (Button) v.findViewById(R.id.floor_button_150);
            floor145Button = (Button) v.findViewById(R.id.floor_button_145);
            actionButtonsContainer = v.findViewById(R.id.action_buttons_container);
            backButton = (Button) actionButtonsContainer.findViewById(R.id.back_button);
            confirmButton = (Button) actionButtonsContainer.findViewById(R.id.confirm_button);
        }
    }
}
