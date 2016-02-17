package com.dii.ids.application.main.navigation;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.dii.ids.application.R;
import com.dii.ids.application.interfaces.OnPositionSelectedListener;

public class NavigationActivity extends AppCompatActivity implements OnPositionSelectedListener {

    public static final String LOG_TAG = NavigationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

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
    public void onPositionSelected(PointF e) {
        FragmentManager fm = getSupportFragmentManager();
        HomeFragment home = (HomeFragment) fm.findFragmentByTag(HomeFragment.FRAGMENT_TAG);
        Log.i(LOG_TAG, home.getTag());
        Bundle args = new Bundle();
        args.putString(HomeFragment.ARG_POSITION, Double.toString(e.y));
        home.getArguments().putAll(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation_content_pane, home);
        transaction.addToBackStack(null);
        transaction.commit();
        // Commit the transaction
//

        /*if (istanceHomeFragment != null) {

        }
        HomeFragment newFragment = new HomeFragment();
        Bundle args = new Bundle();

        args.putString(HomeFragment.ARG_POSITION, Double.toString(e.y));
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.navigation_content_pane, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();*/
    }
}