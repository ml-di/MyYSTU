package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import ru.ystu.myystu.Adapters.SchedulePagerAdapter;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.SettingsController;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.Objects;

public class ScheduleActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String url = "https://www.ystu.ru/information/students/raspisanie-zanyatiy/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        LightStatusBar.setLight(true, true, this, true);

        final Toolbar mToolBar = findViewById(R.id.toolBar_schedule);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolBar.setNavigationOnClickListener(view -> onBackPressed());

        final ViewPager mViewPager = findViewById(R.id.schedule_viewPager);
        final TabLayout mTabLayout = findViewById(R.id.tabLayout_schedule);
        final SchedulePagerAdapter mAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // TODO временно удалить кеш
        File dir = new File(Environment.getExternalStorageDirectory(), "/.MyYSTU/");
        File dir_asf = new File(Environment.getExternalStorageDirectory(), "/.MyYSTU/asf");
        if (dir_asf.exists()) {
            folderDelete(dir, this);
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

    // Удаление всех файлов из каталога
    private static void folderDelete(File directory, Context mContext) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(mContext), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((SettingsActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    file.delete();
                else {
                    folderDelete(file, mContext);
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
