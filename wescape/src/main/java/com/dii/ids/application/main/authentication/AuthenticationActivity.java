package com.dii.ids.application.main.authentication;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;

import com.dii.ids.application.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.idescout.sql.SqlScoutServer;

/**
 * A login screen that offers login via email/password.
 */
public class AuthenticationActivity extends AppCompatActivity {

    private final String TAG = AuthenticationActivity.class.getSimpleName();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API. See
     * https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SqlScoutServer.create(this, getPackageName());
        // Mi assicuro di avere a disposizione l'action bar
        // Va richiamato prima di tutto
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activity);

        if (savedInstanceState == null) {
            LoginFragment loginFragment = new LoginFragment();
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.authentication_content_pane, loginFragment)
                    .addToBackStack(LoginFragment.TAG)
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

    public void hideActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();
    }

    public void showActionBar(String title) {
        assert getSupportActionBar() != null;
        if (title == null) {
            getSupportActionBar().show();
        } else {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().show();
        }
    }
}