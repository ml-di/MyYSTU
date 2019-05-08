package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import ru.ystu.myystu.Fragments.SettingsFragment;
import ru.ystu.myystu.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.toolBar_settings);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (savedInstanceState == null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("settings_fragment");
            if (fragment == null) {
                fragment = new SettingsFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_content, fragment, "settings_fragment")
                    .commit();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {

        final SettingsFragment fragment = new SettingsFragment();
        final Bundle args = new Bundle();

        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
        fragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.activity_slide_right_show,
                                     R.anim.activity_slide_left_out,
                                     R.anim.activity_slide_right_show_reverse,
                                     R.anim.activity_slide_left_out_reverse)
                .replace(R.id.settings_content, fragment, pref.getKey())
                .addToBackStack(pref.getKey())
                .commit();

        mToolbar.setTitle(pref.getTitle());

        return true;
    }

    public void setTitleToolBar (String title) {
        mToolbar.setTitle(title);
    }
}
