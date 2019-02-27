package ru.ystu.myystu;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    private BottomBar mBottomBar;
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

        mFragmentManager = getSupportFragmentManager();
        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        if (savedInstanceState != null) {
            if(mBottomBar.getCurrentTabPosition() == 0)
                mNewsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "state_news_fragment");
        }

        if(mBottomBar.getCurrentTabPosition() == 0
            || mBottomBar.getCurrentTabPosition() == 1){
            // Значки статус бара в черный цвет
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View view = this.getWindow().getDecorView();
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (mBottomBar.getCurrentTabPosition() == 2){
            // Значки статус бара в белый цвет
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View view = this.getWindow().getDecorView();
                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        mBottomBar.setOnTabSelectListener(tabId -> {
            try{
                // Новости
                if(tabId == R.id.tab_news){
                    contentConteiner.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();

                    // Значки статус бара в черный цвет
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        View view = this.getWindow().getDecorView();
                        view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                } else
                    // Уведомления
                    if (tabId == R.id.tab_bell){
                        contentConteiner.setFitsSystemWindows(true);
                        mFragmentManager.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.contentConteiner, mBellFragment, "BELL_FRAGMENT")
                                .commit();

                        // Значки статус бара в черный цвет
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            View view = this.getWindow().getDecorView();
                            view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        }
                    } else
                        // Меню
                        if (tabId == R.id.tab_menu){
                            contentConteiner.setFitsSystemWindows(false);
                            contentConteiner.setPadding(0, 0, 0, 0);
                            mFragmentManager.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .replace(R.id.contentConteiner, mMenuFragment, "MENU_FRAGMENT")
                                    .commit();

                            // Значки статус бара в белый цвет
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                View view = this.getWindow().getDecorView();
                                view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                            }
                        }
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mBottomBar.setOnTabReselectListener(tabId -> {
            switch (tabId){
                case R.id.tab_news:
                    ((NewsFragment) mNewsFragment).scrollTopRecyclerView();
                    break;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("activeTabPosition", mBottomBar.getCurrentTabPosition());
        if(mBottomBar.getCurrentTabPosition() == 0)
            getSupportFragmentManager().putFragment(outState, "state_news_fragment", mNewsFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        int activeTabPosition = savedInstanceState.getInt("activeTabPosition");
        mBottomBar.selectTabAtPosition(activeTabPosition);

        super.onRestoreInstanceState(savedInstanceState);
    }
}
