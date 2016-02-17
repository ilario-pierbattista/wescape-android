package com.dii.ids.application.main.navigation;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dii.ids.application.R;
import com.dii.ids.application.interfaces.OnPositionSelectedListener;

public class NavigationActivity extends AppCompatActivity implements OnPositionSelectedListener {

    public static final String LOG_TAG = NavigationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = HomeFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.navigation_content_pane, homeFragment, HomeFragment.FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositionConfirm(PointF coordinates) {
        FragmentManager fm = getSupportFragmentManager();
        HomeFragment home = (HomeFragment) fm.findFragmentByTag(HomeFragment.FRAGMENT_TAG);

        Bundle args = new Bundle();
        args.putString(HomeFragment.ARG_POSITION, Double.toString(coordinates.y));
        home.getArguments().putAll(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation_content_pane, home)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .addToBackStack(null)
                .commit();
    }
}