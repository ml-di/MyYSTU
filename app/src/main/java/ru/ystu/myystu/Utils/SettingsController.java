package ru.ystu.myystu.Utils;

import android.content.Context;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

public class SettingsController {

    public static boolean isEnabledAnim (Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_ui_animation_enable", true);
    }

    public static boolean isImageDownload (Context mContext) {
        return !PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_experimental_image_enable", false);
    }

    static boolean isImageRAMCache(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_experimental_image_cache_ram", true);
    }

    public static boolean isDarkTheme (Context mContext) {

        final int currentNightMode = mContext.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return true;
        } else {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_ui_darktheme_enable", false);
        }
    }

}
