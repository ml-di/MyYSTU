package ru.ystu.myystu.Utils.BottomFloatingButton.Interface;

import androidx.annotation.DrawableRes;

public interface BottomFloatingButtonInterface {

    void setOnClickListener(OnClickListener listener);

    void setIcon(@DrawableRes int iconRes);

    void setShowDelay (int ms);
    void setAnimation(boolean animation);
    void setClosable(boolean closable);
}
