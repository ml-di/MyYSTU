package ru.ystu.myystu.Utils.BottomFloatingButton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.BottomFloatingButtonInterface;
import ru.ystu.myystu.Utils.BottomFloatingButton.Interface.OnClickListener;

public class BottomFloatingButton implements BottomFloatingButtonInterface {

    private Context mContext;
    private ViewGroup viewGroup;
    private OnClickListener onClickListener;
    private String text;
    private Drawable icon;

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
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public void setIcon(int iconRes) {
        this.icon = mContext.getDrawable(iconRes);
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

        final FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        final BottomFloatingButtonFragment bottomFloatingButtonFragment = new BottomFloatingButtonFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("BFB_TEXT", text);
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
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(viewGroup.getId(), bottomFloatingButtonFragment, "BFB_FRAGMENT")
                    .commit();
        }
    }
}
