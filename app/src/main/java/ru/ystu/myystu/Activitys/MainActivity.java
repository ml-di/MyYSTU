package ru.ystu.myystu.Activitys;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Fragments.BellFragment;
import ru.ystu.myystu.Fragments.MenuFragment;
import ru.ystu.myystu.Fragments.NewsFragment;
import ru.ystu.myystu.R;

public class MainActivity extends FragmentActivity {

    private BottomNavigationView mBottomBar;
    private FragmentManager mFragmentManager;
    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;
    private CoordinatorLayout contentConteiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = findViewById(R.id.bottomBar);
        contentConteiner = findViewById(R.id.contentConteiner);
        contentConteiner.setFitsSystemWindows(true);

        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        if (savedInstanceState == null) {

            mFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                    .commit();

            lightAppBar(true);
        }

        if(mBottomBar.getSelectedItemId() == R.id.tab_news
                || mBottomBar.getSelectedItemId() == R.id.tab_bell){
            lightAppBar(true);
        } else if (mBottomBar.getSelectedItemId() == R.id.tab_menu){
            lightAppBar(false);
        }

        mBottomBar.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                // Новости
                case R.id.tab_news:

                    contentConteiner.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();

                    lightAppBar(true);

                    break;
                // Уведомления
                case R.id.tab_bell:
                    contentConteiner.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mBellFragment, "BELL_FRAGMENT")
                            .commit();

                    lightAppBar(true);
                    break;
                // Меню
                case R.id.tab_menu:
                    contentConteiner.setFitsSystemWindows(false);
                    contentConteiner.setPadding(0, 0, 0, 0);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mMenuFragment, "MENU_FRAGMENT")
                            .commit();

                    lightAppBar(false);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("activeTabPosition", mBottomBar.getSelectedItemId());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        int activeTabPosition = savedInstanceState.getInt("activeTabPosition");
        mBottomBar.setSelectedItemId(activeTabPosition);

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void lightAppBar(boolean isLight){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            final View view = this.getWindow().getDecorView();

            if(isLight){
                // Значки статус бара в черный цвет
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                // Значки статус бара в белый цвет
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
