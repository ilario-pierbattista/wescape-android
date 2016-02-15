package com.dii.ids.application.main.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectionFragment extends BaseFragment {

    private NavigationActivity mActivity;
    private ViewHolder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_selection, container, false);
        holder = new ViewHolder(view);


        // Setup toolbar
        mActivity = (NavigationActivity) getActivity();
        mActivity.setSupportActionBar((Toolbar) view.findViewById(R.id.navigation_standard_toolbar));
        assert mActivity.getSupportActionBar() != null;
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        holder.toolbarTitle.setText(getArguments().getString(BaseFragment.TOOLBAR_TITLE));

        return view;
    }

    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_standard_toolbar);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
    }

}
