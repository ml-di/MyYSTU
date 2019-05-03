package ru.ystu.myystu.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import ru.ystu.myystu.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if(getArguments() != null){
            String key = getArguments().getString("rootKey");
            setPreferencesFromResource(R.xml.preferences, key);
        }else{
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }

    @Override
    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        SettingsFragment applicationPreferencesFragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("rootKey", preferenceScreen.getKey());
        applicationPreferencesFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .replace(getId(), applicationPreferencesFragment)
                .addToBackStack(null)
                .commit();
    }
}
