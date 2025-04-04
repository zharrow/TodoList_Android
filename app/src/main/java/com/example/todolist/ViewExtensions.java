package com.example.todolist;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.card.MaterialCardView;

public class ViewExtensions {

    /**
     * Met à jour la couleur d'un TextView
     */
    public static void setTextViewColor(TextView textView, int colorResId) {
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorResId));
    }

    /**
     * Met à jour la couleur de teinte d'un ImageView
     */
    public static void setImageViewTint(ImageView imageView, int colorResId) {
        ImageViewCompat.setImageTintList(imageView,
                ColorStateList.valueOf(ContextCompat.getColor(imageView.getContext(), colorResId)));
    }

    /**
     * Met à jour la couleur de fond d'une vue
     */
    public static void setBackgroundColor(View view, int colorResId) {
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), colorResId));
    }

    /**
     * Met à jour la couleur de bordure d'une MaterialCardView
     */
    public static void setCardStrokeColor(MaterialCardView cardView, int colorResId) {
        cardView.setStrokeColor(ContextCompat.getColor(cardView.getContext(), colorResId));
    }

    /**
     * Applique une couleur de bordure à une carte selon la priorité
     */
    public static void setCardPriorityColor(MaterialCardView cardView, Priority priority) {
        int colorResId;
        switch (priority) {
            case LOW:
                colorResId = R.color.priority_low;
                break;
            case HIGH:
                colorResId = R.color.priority_high;
                break;
            case URGENT:
                colorResId = R.color.priority_urgent;
                break;
            default:
                colorResId = R.color.priority_medium;
                break;
        }
        cardView.setStrokeColor(ContextCompat.getColor(cardView.getContext(), colorResId));
    }

    /**
     * Applique une couleur à l'indicateur de priorité
     */
    public static void setPriorityIndicatorColor(View indicator, Priority priority) {
        int colorResId;
        switch (priority) {
            case LOW:
                colorResId = R.color.priority_low;
                break;
            case HIGH:
                colorResId = R.color.priority_high;
                break;
            case URGENT:
                colorResId = R.color.priority_urgent;
                break;
            default:
                colorResId = R.color.priority_medium;
                break;
        }
        indicator.setBackgroundColor(ContextCompat.getColor(indicator.getContext(), colorResId));
    }

    /**
     * Retourne le nom de la priorité en français
     */
    public static String getPriorityName(Priority priority, View view) {
        switch (priority) {
            case LOW:
                return view.getContext().getString(R.string.priority_low);
            case HIGH:
                return view.getContext().getString(R.string.priority_high);
            case URGENT:
                return view.getContext().getString(R.string.priority_urgent);
            default:
                return view.getContext().getString(R.string.priority_medium);
        }
    }

    /**
     * Change la visibilité d'une vue avec une animation de fondu
     */
    public static void setVisibilityWithAnimation(View view, boolean visible) {
        if (visible) {
            if (view.getVisibility() != View.VISIBLE) {
                AnimationUtils.fadeIn(view, 300);
            }
        } else {
            if (view.getVisibility() == View.VISIBLE) {
                AnimationUtils.fadeOut(view, 300);
            }
        }
    }
}