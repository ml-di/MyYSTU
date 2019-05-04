package ru.ystu.myystu.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import ru.ystu.myystu.Activitys.SettingsActivity;
import ru.ystu.myystu.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String key;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        this.key = rootKey;

        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        ///Set<String> photoSize = sharedPrefs.getStringSet("preference_general_photoSize", new HashSet<>());

        if(sharedPrefs.getBoolean("preference_notification_enable",true)) {
            PreferenceScreen preferenceScreen = (PreferenceScreen)findPreference(key);
        }*/

        final SwitchPreference enableNotification = findPreference("preference_notification_enable");
        if (enableNotification != null) {
            notificationChange(enableNotification);
            enableNotification.setOnPreferenceClickListener(view -> {
                notificationChange(enableNotification);
                return true;
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(key != null) {
            ((SettingsActivity) getActivity()).setTitleToolBar(getContext()
                            .getResources().getString(R.string.menu_text_settings));
        }
    }

    private void notificationChange(SwitchPreference view){
        if(view.isChecked()) {
            findPreference("preference_notification_ringtone_enable").setEnabled(true);
            findPreference("preference_notification_ringtone").setEnabled(true);
            findPreference("preference_notification_vibration").setEnabled(true);
            findPreference("preference_notification_indicator").setEnabled(true);
            findPreference("preference_notification_push").setEnabled(true);
        } else {
            findPreference("preference_notification_ringtone_enable").setEnabled(false);
            findPreference("preference_notification_ringtone").setEnabled(false);
            findPreference("preference_notification_vibration").setEnabled(false);
            findPreference("preference_notification_indicator").setEnabled(false);
            findPreference("preference_notification_push").setEnabled(false);
        }
    }
}
