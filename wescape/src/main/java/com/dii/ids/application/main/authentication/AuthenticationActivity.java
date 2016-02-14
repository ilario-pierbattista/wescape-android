package com.dii.ids.application.main.authentication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.dii.ids.application.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * A login screen that offers login via email/password.
 */
public class AuthenticationActivity extends AppCompatActivity
        implements SignupFragment.OnFragmentInteractionListener,
        RequestResetFragment.OnFragmentInteractionListener {

    private final String LOG_TAG = AuthenticationActivity.class.getSimpleName();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API. See
     * https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mi assicuro di avere a disposizione l'action bar
        // Va richiamato prima di tutto
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        if (savedInstanceState == null) {
            LoginFragment loginFragment = new LoginFragment();
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.authentication_content_pane, loginFragment)
                    .commit();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.dii.ids.application/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.dii.ids.application/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // @TODO vedere se deve fare qualcosa
    }

    public void hideActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();
    }

    public void showActionBar(String title) {
        assert getSupportActionBar() != null;
        if(title == null) {
            getSupportActionBar().show();
        } else {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().show();
        }
    }
}