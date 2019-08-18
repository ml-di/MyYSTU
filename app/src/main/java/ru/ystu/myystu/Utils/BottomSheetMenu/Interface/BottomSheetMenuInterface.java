package ru.ystu.myystu.Utils.BottomSheetMenu.Interface;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public interface BottomSheetMenuInterface {

    void setOnItemClickListener(OnItemClickListener listener);

    void setTitle(CharSequence title);
    void setTitle(@StringRes int titleRes);

    void setAnimation(boolean animation);
    void setAnimation(boolean animation, @AnimRes int animationRes);

    void setClosable(boolean closable);
    void setIcons(boolean icons);
    void setDividers(boolean dividers);
    void setLightNavigationBar(boolean light);
    void setColorNavigationBar(@ColorRes int navigationBarColorRes);
}
