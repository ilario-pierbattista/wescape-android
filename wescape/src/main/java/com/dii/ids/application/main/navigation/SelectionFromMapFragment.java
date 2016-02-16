package com.dii.ids.application.main.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;
import com.dii.ids.application.main.navigation.tasks.MapsDownloaderTask;

public class SelectionFromMapFragment extends BaseFragment {
    private static final String LOG_TAG = SelectionFromMapFragment.class.getSimpleName();
    private ViewHolder holder;
    private MapsDownloaderTask mapsTask;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_selection_from_map, container, false);
        holder = new ViewHolder(view);

        holder.buttonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flootButtonListener(v);
            }
        });

        return view;
    }

    public void flootButtonListener(View v) {
        Button button = (Button) v;
        Log.i(LOG_TAG, button.getText().toString());
    }

    public class ViewHolder {
        public final SubsamplingScaleImageView mapView;
        public final RelativeLayout buttonContainer;

        public ViewHolder(View v) {
            mapView = (SubsamplingScaleImageView) v.findViewById(R.id.navigation_map_image);
            buttonContainer = (RelativeLayout) v.findViewById(R.id.floor_buttons_container);
        }
    }
}
