package ru.ystu.myystu.Utils;

import android.content.Context;

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
}
