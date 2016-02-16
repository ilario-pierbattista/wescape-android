package com.dii.ids.application.main.navigation;

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
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

public class SelectionFromMapFragment extends MapFragment {
    private static final String LOG_TAG = SelectionFromMapFragment.class.getSimpleName();
    private MapsDownloaderTask mapsTask;
    private ViewHolder holder;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection_from_map, container, false);
        holder = new ViewHolder(view);

        mapsTask = new MapsDownloaderTask()
                .inject(this);
        mapsTask.execute(155);

        // @TODO trovare una soluzione più elegante per questi listeners
        holder.floor155Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flootButtonListener(v);
            }
        });
        holder.floor150Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flootButtonListener(v);
            }
        });
        holder.floor145Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flootButtonListener(v);
            }
        });

        // Setup back button
        holder.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onTaskSuccess(MapsDownloaderTask asyncTask) {
        final Bitmap image = mapsTask.getImage();
        this.mapsTask = null;

        holder.mapView.setImage(ImageSource.bitmap(image));
        holder.mapView.setMinimumDpi(40);

        // @TODO Spostare il gestore della gesture nel fragment di competenza
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(holder.mapView.isReady()) {
                    PointF sCoord = holder.mapView.viewToSourceCoord(e.getX(), e.getY());
                    Toast.makeText(getActivity().getApplicationContext(), "Tap on [" +
                            Double.toString(sCoord.x) + "," + Double.toString(sCoord.y), Toast.LENGTH_SHORT).show();
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

    public void flootButtonListener(View v) {
        Button button = (Button) v;
        holder.floor155Button.setSelected(false);
        holder.floor150Button.setSelected(false);
        holder.floor145Button.setSelected(false);
        v.setSelected(true);
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

        public ViewHolder(View v) {
            mapView = (SubsamplingScaleImageView) v.findViewById(R.id.navigation_map_image);
            floor155Button = (Button) v.findViewById(R.id.floor_button_155);
            floor150Button = (Button) v.findViewById(R.id.floor_button_150);
            floor145Button = (Button) v.findViewById(R.id.floor_button_145);
            actionButtonsContainer = v.findViewById(R.id.action_buttons_container);
            backButton = (Button) actionButtonsContainer.findViewById(R.id.back_button);
        }
    }
}
