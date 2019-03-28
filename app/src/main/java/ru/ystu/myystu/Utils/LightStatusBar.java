package ru.ystu.myystu.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ru.ystu.myystu.R;

public class LightStatusBar {

    public static void setLight(boolean isLight, Activity mActivity){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final View view = mActivity.getWindow().getDecorView();

            if(isLight){
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            final Window win = mActivity.getWindow();
            final WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            if(isLight)
                mActivity.getWindow().setStatusBarColor(mActivity.getResources().getColor(R.color.colorLightStatusBar));
            else
                mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        /*
            if (isLight) {
                winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            }
        * */
    }

}
