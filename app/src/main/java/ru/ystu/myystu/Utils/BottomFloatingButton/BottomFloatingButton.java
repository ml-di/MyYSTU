package ru.ystu.myystu.Utils.BottomFloatingButton;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.BottomFloatingButtonInterface;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.OnClickListener;

public class BottomFloatingButton implements BottomFloatingButtonInterface {

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
        new Handler().postDelayed(this::show, delay);
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

    private void show() {

        if (text.length() > 0) {
            final FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            final BottomFloatingButtonFragment bottomFloatingButtonFragment = new BottomFloatingButtonFragment();
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

                @AnimRes final int show;
                @AnimRes final int hide;

                if (!isAnimation) {
                    show = 0;
                    hide = 0;
                } else {
                    show = R.anim.bfb_show;
                    hide = R.anim.bfb_hide;
                }

                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(show, hide)
                        .add(viewGroup.getId(), bottomFloatingButtonFragment, "BFB_FRAGMENT")
                        .commit();

                bottomFloatingButtonFragment.setOnClickListener(() -> {
                    onClickListener.OnClick();
                    if (isClosable) {
                        fragmentManager
                                .beginTransaction()
                                .setCustomAnimations(show, hide)
                                .remove(bottomFloatingButtonFragment)
                                .commit();
                    }
                });
            }
        }
    }


    static class SaveListener {

        private static OnClickListener onClickListener;

        static OnClickListener onRequestListener() {
            return onClickListener;
        }

        static void onSaveListener (OnClickListener listener) {
            onClickListener = listener;
        }

    }
}
