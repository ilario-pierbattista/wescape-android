package com.dii.ids.application.main.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;

public class SelectionFromMapFragment extends BaseFragment {
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

        return view;
    }

}
