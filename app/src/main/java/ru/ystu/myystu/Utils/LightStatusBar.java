package ru.ystu.myystu.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.content.ContextCompat;
import ru.ystu.myystu.R;

public class LightStatusBar {

    public static void setLight(boolean isLightStatusBar, boolean isLightNavigationBar, Activity mActivity){

        /*
        *       StatusBar
        */
        final View view = mActivity.getWindow().getDecorView();
        final Window win = mActivity.getWindow();
        final WindowManager.LayoutParams winParams = win.getAttributes();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(isLightStatusBar){
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            if(isLightStatusBar)
                mActivity.getWindow().setStatusBarColor(mActivity.getResources().getColor(R.color.colorLightStatusBar));
            else
                mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        /*
         *       NavigationBar
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(isLightNavigationBar){
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                win.setNavigationBarColor(ContextCompat.getColor(mActivity, R.color.colorBackground));
            } else {
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }
    }
}
