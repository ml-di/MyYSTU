package ru.ystu.myystu.Activitys;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import ru.ystu.myystu.Fragments.BellFragment;
import ru.ystu.myystu.Fragments.MenuFragment;
import ru.ystu.myystu.Fragments.NewsFragment;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Services.UpdateCheck;
import ru.ystu.myystu.Utils.BottomBarHelper;
import ru.ystu.myystu.Utils.LightStatusBar;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private ArrayList<String> updateList;

    private BottomNavigationView mBottomBar;
    private FragmentManager mFragmentManager;
    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;
    private CoordinatorLayout mContentContainer;

    private boolean updateEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = findViewById(R.id.bottomBar);
        mContentContainer = findViewById(R.id.contentContainer);
        mContentContainer.setFitsSystemWindows(true);

        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        mSharedPreferences = getSharedPreferences("UPDATE_LIST", Context.MODE_PRIVATE);

        if (savedInstanceState == null) {

            updateList = new ArrayList<>();
            mFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.contentContainer, mNewsFragment, "NEWS_FRAGMENT")
                    .commit();
            LightStatusBar.setLight(true, this);
            startService();
        }

        if(mBottomBar.getSelectedItemId() == R.id.tab_news
                || mBottomBar.getSelectedItemId() == R.id.tab_bell){
            LightStatusBar.setLight(true, this);
        } else if (mBottomBar.getSelectedItemId() == R.id.tab_menu){
            LightStatusBar.setLight(false, this);
        }

        badgeChange(mSharedPreferences.getAll().size());

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        updateEnabled = sharedPrefs.getBoolean("preference_additional_update_enable", true);

        mBottomBar.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                // Новости
                case R.id.tab_news:

                    mContentContainer.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentContainer, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, this);

                    break;
                // Уведомления
                case R.id.tab_bell:

                    mContentContainer.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentContainer, mBellFragment, "BELL_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, this);

                    break;
                // Меню
                case R.id.tab_menu:
                    mContentContainer.setFitsSystemWindows(false);
                    mContentContainer.setPadding(0, 0, 0, 0);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentContainer, mMenuFragment, "MENU_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(false, this);
                    break;
            }

            return true;
        });

        mBottomBar.setOnNavigationItemReselectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.tab_news:
                    ((NewsFragment) mNewsFragment).scrollTopRecyclerView();
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            badgeChange(mSharedPreferences.getAll().size());
            // Обновить уведомления если вкладка с ними открыта
            if(mBottomBar.getSelectedItemId() == R.id.tab_bell){
                if(mSharedPreferences.getAll().size() > 0){

                    for(Map.Entry<String, ?> entry : mSharedPreferences.getAll().entrySet()) {
                        final int type = Integer.parseInt(entry.getKey().substring(0, 1));
                        final int id = Integer.parseInt(entry.getKey().substring(1, 2));
                        final String text = (String) entry.getValue();
                        updateList.add(type + "" + id + "" + text);
                    }

                    ((BellFragment) mBellFragment).updateRecycler(updateList);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("selItemId", mBottomBar.getSelectedItemId());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        badgeChange(mSharedPreferences.getAll().size());
        mBottomBar.setSelectedItemId(savedInstanceState.getInt("selItemId"));

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void badgeChange (int count){

        if(count > 0)
            BottomBarHelper.showBadge(this, mBottomBar, R.id.tab_bell, count);
        else
            BottomBarHelper.removeBadge(this, mBottomBar, R.id.tab_bell);
    }
    public void showBottomBar (boolean isShow) {

        final CoordinatorLayout.LayoutParams paramsBottomBar = (CoordinatorLayout.LayoutParams) mBottomBar.getLayoutParams();
        final CoordinatorLayout.LayoutParams paramsMainLayout = (CoordinatorLayout.LayoutParams) mContentContainer.getLayoutParams();
        if(isShow){
            paramsBottomBar.setBehavior(null);
            paramsMainLayout.setMargins(0, 0, 0, mBottomBar.getHeight());
        } else {
            paramsBottomBar.setBehavior(new HideBottomViewOnScrollBehavior());
            paramsMainLayout.setMargins(0, 0, 0, 0);
        }
    }

    private void startService() {
        if(updateEnabled) {
            // Запуск сервиса для проверки обновлений
            final PendingIntent mPendingIntent = createPendingResult(1, new Intent(), 0);
            final Intent mIntent = new Intent(this, UpdateCheck.class)
                    .putExtra("pending", mPendingIntent);

            startService(mIntent);
        }
    }
}
