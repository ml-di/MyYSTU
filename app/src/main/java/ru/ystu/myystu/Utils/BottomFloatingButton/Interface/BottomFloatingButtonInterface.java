package ru.ystu.myystu.Utils.BottomFloatingButton.Interface;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.FragmentManager;

public interface BottomFloatingButtonInterface {

    void setOnClickListener(OnClickListener listener);

    void setIcon(@DrawableRes int iconRes);

    void setShowDelay (int ms);
    void setAnimation(boolean animation);
    void setClosable(boolean closable);

    void updateFragmentManager(FragmentManager fragmentManager);

    void show();
    void hide();
}
