package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import ru.ystu.myystu.Fragments.SettingsFragment;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.SettingsController;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LightStatusBar.setLight(true, true, this, true);

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
    protected void onPause() {
        super.onPause();
        if (isFinishing() && !SettingsController.isEnabledAnim(this)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {

        final SettingsFragment fragment = new SettingsFragment();
        final Bundle args = new Bundle();

        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
        fragment.setArguments(args);

        int mEnterAnim = R.anim.activity_slide_right_show;
        int mExitAnim = R.anim.activity_slide_left_out;
        int mPopEnterAnim = R.anim.activity_slide_right_show_reverse;
        int mPopExitAnim = R.anim.activity_slide_left_out_reverse;

        if (!SettingsController.isEnabledAnim(this)) {
            mEnterAnim = 0;
            mExitAnim = 0;
            mPopEnterAnim = 0;
            mPopExitAnim = 0;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(mEnterAnim,
                        mExitAnim,
                        mPopEnterAnim,
                        mPopExitAnim)
                .replace(R.id.settings_content, fragment, pref.getKey())
                .addToBackStack(pref.getKey())
                .commit();

        mToolbar.setTitle(pref.getTitle());

        return true;
    }

    public void startFragment (Fragment fragment, Preference pref) {
        openFragment(fragment, pref.getKey(), pref.getTitle().toString());
    }

    public void startFragment (Fragment fragment, String TAG, String title) {
        openFragment(fragment, TAG, title);
    }

    private void openFragment(Fragment fragment, String TAG, String title) {

        int mEnterAnim = R.anim.activity_slide_right_show;
        int mExitAnim = R.anim.activity_slide_left_out;
        int mPopEnterAnim = R.anim.activity_slide_right_show_reverse;
        int mPopExitAnim = R.anim.activity_slide_left_out_reverse;

        if (!SettingsController.isEnabledAnim(this)) {
            mEnterAnim = 0;
            mExitAnim = 0;
            mPopEnterAnim = 0;
            mPopExitAnim = 0;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .setCustomAnimations(mEnterAnim, mExitAnim,
                        mPopEnterAnim, mPopExitAnim)
                .replace(R.id.settings_content, fragment, TAG)
                .addToBackStack(TAG)
                .commit();

        mToolbar.setTitle(title);
    }

    public void setTitleToolBar (String title) {
        mToolbar.setTitle(title);
    }
}
