package com.example.silkroad_iot.ui.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.example.silkroad_iot.R;

/**
 * Utility class for applying animations to UI elements
 */
public class AnimationHelper {

    /**
     * Fade in animation for views
     */
    public static void fadeIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Fade out animation for views
     */
    public static void fadeOut(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Slide in from bottom animation
     */
    public static void slideInBottom(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_bottom);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Slide in from right animation
     */
    public static void slideInRight(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_right);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Scale up animation (for buttons and cards)
     */
    public static void scaleUp(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_up);
        view.startAnimation(animation);
    }

    /**
     * Bounce animation (for buttons)
     */
    public static void bounce(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        view.startAnimation(animation);
    }

    /**
     * Apply activity enter animation
     */
    public static void applyActivityEnterAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    /**
     * Apply activity exit animation
     */
    public static void applyActivityExitAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
    }

    /**
     * Animate multiple views in sequence with delay
     */
    public static void animateSequence(View... views) {
        long delay = 0;
        for (View view : views) {
            view.setAlpha(0f);
            view.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(delay)
                .start();
            delay += 100;
        }
    }

    /**
     * Apply shimmer loading animation
     */
    public static void startShimmer(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.shimmer_animation);
        view.startAnimation(animation);
    }

    /**
     * Stop shimmer animation
     */
    public static void stopShimmer(View view) {
        view.clearAnimation();
    }
}
