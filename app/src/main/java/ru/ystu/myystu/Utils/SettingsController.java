package ru.ystu.myystu.Utils;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class SettingsController {

    public static boolean isEnabledAnim (Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_ui_animation_enable", true);
    }

    public static boolean isEnabledUpdate (Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("preference_additional_update_enable", true);
    }

}
