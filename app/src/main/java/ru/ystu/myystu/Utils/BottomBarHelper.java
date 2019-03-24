package ru.ystu.myystu.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatTextView;
import ru.ystu.myystu.R;

public class BottomBarHelper {

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, int value) {

        final BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        final View badge = LayoutInflater.from(context).inflate(R.layout.badge_layout, bottomNavigationView, false);
        final AppCompatTextView text = badge.findViewById(R.id.badge_text);
        Animation show = AnimationUtils.loadAnimation(context, R.anim.badge_show);

        if(value > 9)
            text.setText("9+");
        else
            text.setText("" + value);

        if(itemView.getChildCount() < 3){
            itemView.addView(badge);
            badge.startAnimation(show);
        } else {
            // TODO не перекрывать анимацию появления
            itemView.removeViewAt(2);
            itemView.addView(badge);

        }
    }

    public static void removeBadge(Context context, BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);

        if(itemView.getChildCount() == 3){

            View badge = itemView.getChildAt(2);
            Animation remove = AnimationUtils.loadAnimation(context, R.anim.badge_hide);
            badge.startAnimation(remove);

            itemView.removeViewAt(2);
        }
    }

}
