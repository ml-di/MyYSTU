package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import ru.ystu.myystu.Fragments.SettingsFragment;
import ru.ystu.myystu.R;

import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_content, new SettingsFragment()).commit();
    }
}
