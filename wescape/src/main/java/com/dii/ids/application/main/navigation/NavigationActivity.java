package com.dii.ids.application.main.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.dii.ids.application.R;
import com.dii.ids.application.main.BaseFragment;
import com.google.firebase.iid.FirebaseInstanceId;

public class NavigationActivity extends AppCompatActivity {

    public static final String TAG = NavigationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());

        boolean emergency = false;
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("emergency")) {
                    emergency = true;
                }
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
        Log.d(TAG, String.valueOf(emergency));
        if (savedInstanceState == null) {
            HomeFragment homeFragment = HomeFragment.newInstance(emergency);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.navigation_content_pane, homeFragment, HomeFragment.TAG)
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

    /**
     * Cambia il fragment
     *
     * @param fragment
     */
    public void changeFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.navigation_content_pane, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(fragment.TAG)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter(HomeFragment.EMERGENCY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }

    /**
     * Manager dei messaggi broadcast
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO andrebbe gestito meglio, al momento ci si puo imbattere in stackoverflow
            HomeFragment homeFragment = HomeFragment.newInstance(true);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.navigation_content_pane, homeFragment, HomeFragment.TAG)
                    .commit();
        }
    };
}