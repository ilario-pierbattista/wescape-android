package com.dii.ids.application.main.navigation;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dii.ids.application.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NavigationActivityFragment extends Fragment {

    public NavigationActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }
}
