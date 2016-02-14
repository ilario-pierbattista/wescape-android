package com.dii.ids.application.main.navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.dii.ids.application.R;

public class NavigationActivity extends AppCompatActivity {

    private ViewHolder holder;
    private boolean emergency = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        holder = new ViewHolder(findViewById(android.R.id.content));


        // Set the Toolbar as the ActionBar
        setSupportActionBar(holder.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup textview text
        holder.destinationViewPlaceholder.setText(R.string.navigation_going_to);
        holder.originViewText.setText(R.string.navigation_select_origin);
        holder.destinationViewText.setText(R.string.navigation_select_destination);

        // Set the Fab button
        holder.startFabButton.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Update view for normal/emergency state
     */
    private void toogleEmergency() {
        if (!emergency) {
            animateAppAndStatusBar(R.color.regularBlue, R.color.regularRed);
            holder.startFabButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.regularRed)));
            holder.toolbarTitle.setText(R.string.action_emergency);
            holder.destinationViewText.setText("La destinazione verrà impostata automaticamente");
            emergency = true;
        } else {
            animateAppAndStatusBar(R.color.regularRed, R.color.regularBlue);
            holder.startFabButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.regularBlue)));
            holder.toolbarTitle.setText(R.string.title_activity_navigation);
            holder.destinationViewText.setText(R.string.navigation_select_destination);
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
                holder.revealView,
                holder.toolbar.getWidth() / 2,
                holder.toolbar.getHeight() / 2, 0,
                holder.toolbar.getWidth() / 2);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.revealView.setBackgroundColor(getResources().getColor(toColor));
            }
        });

        holder.revealBackgroundView.setBackgroundColor(getResources().getColor(fromColor));
        animator.setStartDelay(50);
        animator.setDuration(125);
        animator.start();
        holder.revealView.setVisibility(View.VISIBLE);
    }

    /**
     * Classe wrapper degli elementi della vista
     */
    public static class ViewHolder {
        public final Toolbar toolbar;
        public final TextView toolbarTitle;
        public final FloatingActionButton startFabButton;
        public final View revealView;
        public final View revealBackgroundView;
        public final View destinationView;
        public final View originView;
        public final TextView destinationViewText;
        public final TextView originViewText;
        public final TextView destinationViewPlaceholder;
        public final TextView originViewPlaceholder;


        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.navigation_toolbar);
            toolbarTitle = (TextView) view.findViewById(R.id.navigation_toolbar_textview_title);
            startFabButton = (FloatingActionButton) view.findViewById(R.id.navigation_fab_start);
            revealView = view.findViewById(R.id.reveal_view);
            revealBackgroundView = view.findViewById(R.id.reveal_background_view);

            destinationView = view.findViewById(R.id.navigation_input_destination);
            destinationViewText = (TextView) destinationView.findViewById(R.id.text);
            destinationViewPlaceholder = (TextView) destinationView.findViewById(R.id.placeholder);
            originView = view.findViewById(R.id.navigation_input_origin);
            originViewText = (TextView) originView.findViewById(R.id.text);
            originViewPlaceholder = (TextView) originView.findViewById(R.id.placeholder);

        }
    }
}
