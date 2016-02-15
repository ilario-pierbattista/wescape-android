package com.dii.ids.application.animations;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * FabAnimation class
 */
public class FabAnimation {
    private Fragment fragment;

    public FabAnimation(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Change FloatingActionButton color using a nice animation
     *
     * @param fab     FloatingActionButton
     * @param toColor Arriving color
     * @param toIcon  Arriving icon
     */
    public void animateFab(final FloatingActionButton fab, final int toColor, final int toIcon) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f,
                                                   Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(fragment.getResources().getColorStateList(toColor));
                if (toIcon >= 0) {
                    fab.setImageDrawable(fragment.getResources().getDrawable(toIcon, null));
                }

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                                                           Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }


    public void animateFab(final FloatingActionButton fab, final int toColor) {
        animateFab(fab, toColor, -1);
    }
}
