package com.dii.ids.application.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Classe per la realizzazione delle animazioni della toolbar
 */
public class ToolbarAnimation {
    private View revealView;
    private View revealBackgroundView;
    private Toolbar toolbar;

    public ToolbarAnimation(View revealView, View revealBackgroundView, Toolbar toolbar) {
        this.revealView = revealView;
        this.revealBackgroundView = revealBackgroundView;
        this.toolbar = toolbar;
    }

    /**
     * Change Toolbar and StatusBar color using a nice animation
     *
     * @param fromColor Starting color
     * @param toColor   Arriving color
     */
    public void animateAppAndStatusBar(final int fromColor, final int toColor) {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                revealView,
                toolbar.getWidth() / 2,
                toolbar.getHeight() / 2, 0,
                toolbar.getWidth() / 2);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                revealView.setBackgroundColor(toColor);
            }
        });

        revealBackgroundView.setBackgroundColor(fromColor);
        animator.setStartDelay(70);
        animator.setDuration(125);
        animator.start();
        revealView.setVisibility(View.VISIBLE);
    }
}
