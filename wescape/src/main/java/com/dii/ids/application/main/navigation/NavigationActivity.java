package com.dii.ids.application.main.navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.dii.ids.application.R;

public class NavigationActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private View mRevealView;
    private View mRevealBackgroundView;
    private boolean emergency = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Set the Toolbar as the ActionBar
        mToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.navigation_toolbar_textview_title);
        mRevealView = findViewById(R.id.reveal);
        mRevealBackgroundView = findViewById(R.id.revealBackground);


        // Set the Fab button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_emergency:
                toogleEmergency();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toogleEmergency() {
        if (!emergency) {
            animateAppAndStatusBar(R.color.regularBlue, R.color.regularRed);
            mToolbarTitle.setText(R.string.action_emergency);
            emergency = true;
        } else {
            mToolbarTitle.setText(R.string.title_activity_navigation);
            animateAppAndStatusBar(R.color.regularRed, R.color.regularBlue);
            emergency = false;
        }
    }

    /**
     * Change Toolbar and StatusBar color using a nice animation
     *
     * @param fromColor Starting color
     * @param toColor Arriving color
     */
    private void animateAppAndStatusBar(int fromColor, final int toColor) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mRevealView,
                mToolbar.getWidth() / 2,
                mToolbar.getHeight() / 2, 0,
                mToolbar.getWidth() / 2);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRevealView.setBackgroundColor(getResources().getColor(toColor));
            }
        });

        mRevealBackgroundView.setBackgroundColor(getResources().getColor(fromColor));
        animator.setStartDelay(50);
        animator.setDuration(125);
        animator.start();
        mRevealView.setVisibility(View.VISIBLE);
    }
}
