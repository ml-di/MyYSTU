package ru.ystu.myystu.Utils;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class NewsPhotoPagerTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.9f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        final int pageWidth = page.getWidth();
        final int pageHeight = page.getHeight();

        if (position >= -1 && position <= 1) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }

            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        }
    }
}
