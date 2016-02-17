package com.dii.ids.application.main.navigation;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dii.ids.application.R;

public class NavigationActivity extends AppCompatActivity {

    public static final String LOG_TAG = NavigationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        if (savedInstanceState == null) {
            HomeFragment homeFragment = HomeFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.navigation_content_pane, homeFragment, HomeFragment.FRAGMENT_TAG)
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
}