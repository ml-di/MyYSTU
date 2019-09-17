package ru.ystu.myystu.Utils.BottomFloatingButton.Interface;

import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public interface BottomFloatingButtonInterface {

    void setOnClickListener(OnClickListener listener);

    void setIcon(Drawable icon);
    void setIcon(@DrawableRes int iconRes);

    void setAnimation(boolean animation);
    void setClosable(boolean closable);

    void show();
}
