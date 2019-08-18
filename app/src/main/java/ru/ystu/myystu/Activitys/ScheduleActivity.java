package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import ru.ystu.myystu.Adapters.SchedulePagerAdapter;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.SettingsController;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;

public class ScheduleActivity extends AppCompatActivity {

    private String url = "https://www.ystu.ru/learning/schedule/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        LightStatusBar.setLight(true, true, this);

        final Toolbar mToolBar = findViewById(R.id.toolBar_schedule);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolBar.setNavigationOnClickListener(view -> onBackPressed());

        final ViewPager mViewPager = findViewById(R.id.schedule_viewPager);
        final TabLayout mTabLayout = findViewById(R.id.tabLayout_schedule);
        final SchedulePagerAdapter mAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && !SettingsController.isEnabledAnim(this)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_schedule_openInBrowser){
            final Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(mIntent);
        }
        return true;
    }
}
