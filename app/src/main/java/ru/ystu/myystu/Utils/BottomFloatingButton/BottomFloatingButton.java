package ru.ystu.myystu.Utils.BottomFloatingButton;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.annotation.AnimRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.BottomFloatingButtonInterface;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.OnClickListener;

public class BottomFloatingButton implements BottomFloatingButtonInterface {

    private FragmentManager fragmentManager;
    private BottomFloatingButtonFragment bottomFloatingButtonFragment;

    private Context mContext;
    private ViewGroup viewGroup;
    private OnClickListener onClickListener;
    private String text;
    private @DrawableRes int icon = -1;
    private int delay = 0;

    private boolean isAnimation = true;
    private boolean isClosable = true;

    public BottomFloatingButton(Context mContext, ViewGroup rootViewGroup, String text) {
        this.mContext = mContext;
        this.viewGroup = rootViewGroup;
        this.text = text;
    }

    public BottomFloatingButton(Context mContext, ViewGroup rootViewGroup, @StringRes int textRes) {
        this.mContext = mContext;
        this.viewGroup = rootViewGroup;
        this.text = mContext.getString(textRes);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    @Override
    public void setIcon(int iconRes) {
        this.icon = iconRes;
    }

    @Override
    public void setShowDelay(int ms) {
        this.delay = ms;
    }

    @Override
    public void setAnimation(boolean animation) {
        this.isAnimation = animation;
    }

    @Override
    public void setClosable(boolean closable) {
        this.isClosable = closable;
    }

    @Override
    public void show() {

        new Handler().postDelayed(() -> {
            if (text.length() > 0) {

                fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                bottomFloatingButtonFragment = new BottomFloatingButtonFragment();

                final Bundle bundle = new Bundle();
                bundle.putString("BFB_TEXT", text);
                bundle.putInt("BFB_ICON", icon);
                bottomFloatingButtonFragment.setArguments(bundle);

                if(fragmentManager.getFragments().size() > 0){
                    for(Fragment fragment : fragmentManager.getFragments()){
                        if (fragment != null)
                            fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    fragmentManager.getFragments().clear();
                }

                if (viewGroup != null) {

                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(getShowAnim(), getHideAnim())
                            .add(viewGroup.getId(), bottomFloatingButtonFragment, "BFB_FRAGMENT")
                            .commit();

                    bottomFloatingButtonFragment.setOnClickListener(() -> {
                        if (onClickListener != null) {
                            onClickListener.OnClick();
                            if (isClosable) {
                                closeFragment(bottomFloatingButtonFragment, getShowAnim(), getHideAnim());
                            }
                        }
                    });
                }
            }
        }, delay);
    }

    @Override
    public void hide() {
        if (bottomFloatingButtonFragment != null) {
            closeFragment(bottomFloatingButtonFragment, getShowAnim(), getHideAnim());
        }
    }

    private void closeFragment(Fragment fragment, @AnimRes int showAnim, @AnimRes int hideAnim) {

        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(showAnim, hideAnim)
                    .remove(fragment)
                    .commit();
        }
    }

    @Override
    public void updateFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    private @AnimRes int getShowAnim () {
        if (isAnimation) {
            return R.anim.bfb_show;
        } else {
            return 0;
        }
    }

    private @AnimRes int getHideAnim () {
        if (isAnimation) {
            return R.anim.bfb_hide;
        } else {
            return 0;
        }
    }

    public static class onSaveInstance {

        static private BottomFloatingButton bottomFloatingButton;

        public static void setBottomFloatingButton (BottomFloatingButton bfb) {
            bottomFloatingButton = bfb;
        }

        public static BottomFloatingButton getBottomFloatingButton () {
            return bottomFloatingButton;
        }
    }
}
