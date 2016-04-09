package com.dii.ids.application.animations;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * FabAnimation class
 */
public class FabAnimation {
    /**
     * Change FloatingActionButton color using a nice animation
     *
     * @param fab     FloatingActionButton
     * @param toColor Arriving color
     */
    public void animateFab(final FloatingActionButton fab, ColorStateList toColor) {
        animateFab(fab, toColor, null);
    }

    /**
     * Change FloatingActionButton color using a nice animation
     *
     * @param fab     FloatingActionButton
     * @param toColor Arriving color
     * @param toIcon  Arriving icon
     */
    public void animateFab(final FloatingActionButton fab, final ColorStateList toColor, final Drawable toIcon) {
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
                fab.setBackgroundTintList(toColor);
                if (toIcon != null) {
                    fab.setImageDrawable(toIcon);
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
}
