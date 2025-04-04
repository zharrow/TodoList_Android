package com.example.todolist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

public class AnimationUtils {

    /**
     * Ajoute une animation de rebond quand un élément est cliqué
     * @param view La vue à animer
     */
    public static void applyClickAnimation(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setInterpolator(new DecelerateInterpolator())
                        .start())
                .start();
    }

    /**
     * Anime l'apparition d'une vue avec un effet de fade in
     * @param view La vue à animer
     * @param duration Durée de l'animation en ms
     */
    public static void fadeIn(View view, int duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Anime la disparition d'une vue avec un effet de fade out
     * @param view La vue à animer
     * @param duration Durée de l'animation en ms
     */
    public static void fadeOut(View view, int duration) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    /**
     * Anime une vue pour qu'elle apparaisse en glissant du bas
     * @param view La vue à animer
     * @param duration Durée de l'animation en ms
     */
    public static void slideInFromBottom(View view, int duration) {
        view.setTranslationY(view.getHeight());
        view.setVisibility(View.VISIBLE);
        view.animate()
                .translationY(0)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Anime une vue pour qu'elle disparaisse en glissant vers le bas
     * @param view La vue à animer
     * @param duration Durée de l'animation en ms
     */
    public static void slideOutToBottom(View view, int duration) {
        view.animate()
                .translationY(view.getHeight())
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    /**
     * Anime le FloatingActionButton pour le faire apparaître
     * @param fab Le FloatingActionButton à animer
     */
    public static void showFab(View fab) {
        Animation anim = android.view.animation.AnimationUtils.loadAnimation(fab.getContext(), R.anim.fab_show);
        fab.startAnimation(anim);
        fab.setVisibility(View.VISIBLE);
    }

    /**
     * Anime le FloatingActionButton pour le faire disparaître
     * @param fab Le FloatingActionButton à animer
     */
    public static void hideFab(View fab) {
        Animation anim = android.view.animation.AnimationUtils.loadAnimation(fab.getContext(), R.anim.fab_hide);
        fab.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Applique une animation de layout pour la RecyclerView
     * @param recyclerView RecyclerView à animer
     */
    public static void setRecyclerViewAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        android.view.animation.LayoutAnimationController animation =
                android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_layout);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Anime la hauteur d'une vue de 0 à sa hauteur réelle
     * @param view Vue à animer
     * @param duration Durée de l'animation
     */
    public static void expandView(View view, int duration) {
        view.setVisibility(View.VISIBLE);

        int targetHeight = view.getMeasuredHeight();

        // Si la vue n'a pas encore été mesurée
        if (targetHeight == 0) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    ((View) view.getParent()).getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(widthSpec, heightSpec);
            targetHeight = view.getMeasuredHeight();
        }

        // Animation
        view.getLayoutParams().height = 0;
        view.requestLayout();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * Anime la hauteur d'une vue de sa hauteur réelle à 0
     * @param view Vue à animer
     * @param duration Durée de l'animation
     */
    public static void collapseView(View view, int duration) {
        int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
    }
}