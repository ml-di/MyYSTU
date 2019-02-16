package ru.ystu.myystu;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    //region переменные
    private BottomBar mBottomBar;

    private FragmentManager mFragmentManager;

    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;

    private int colorInActiveTab;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region инициализация объектов
        mBottomBar = findViewById(R.id.bottomBar);


        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();
        //endregion

        if (savedInstanceState != null) {
            if(mBottomBar.getCurrentTabPosition() == 0)
                mNewsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "state_news_fragment");
        }

        if(mBottomBar.getCurrentTabPosition() == 0){
            // Значки статус бара в черный цвет
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View view = this.getWindow().getDecorView();
                view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        // Работа с цветами иконок бара при переключении
        mBottomBar.setTabSelectionInterceptor((oldTabId, newTabId) -> {

            if (newTabId == R.id.tab_menu) {
                mBottomBar.setInActiveTabColor(Color.parseColor("#ffffff"));
                colorInActiveTab = Color.parseColor("#ffffff");
                return false;
            } else
            if(newTabId == R.id.tab_news){
                mBottomBar.setInActiveTabColor(Color.parseColor("#000000"));
                colorInActiveTab = Color.parseColor("#000000");

                // Значки статус бара в черный цвет
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View view = this.getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

                return false;
            } else
            if (newTabId == R.id.tab_bell){
                mBottomBar.setInActiveTabColor(Color.parseColor("#000000"));
                colorInActiveTab = Color.parseColor("#000000");
                return false;
            }

            return true;
        });

        mBottomBar.setOnTabSelectListener(tabId -> {

            try{
                // Новости
                if(tabId == R.id.tab_news){

                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();
                } else
                    // Уведомления
                    if (tabId == R.id.tab_bell){

                        mFragmentManager.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.contentConteiner, mBellFragment, "BELL_FRAGMENT")
                                .commit();

                    } else
                        // Меню
                        if (tabId == R.id.tab_menu){

                            mFragmentManager.beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .replace(R.id.contentConteiner, mMenuFragment, "MENU_FRAGMENT")
                                    .commit();

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

        // Сохранение состояния цветов bottomBar
        outState.putInt("activeTabPosition", mBottomBar.getCurrentTabPosition());
        if(mBottomBar.getCurrentTabPosition() == 0)
            getSupportFragmentManager().putFragment(outState, "state_news_fragment", mNewsFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Восстановление состояние цветов bottomBar
        int activeTabPosition = savedInstanceState.getInt("activeTabPosition");
        if(activeTabPosition == 2)
            mBottomBar.setInActiveTabColor(Color.parseColor("#ffffff"));
        else
            mBottomBar.setInActiveTabColor(Color.parseColor("#000000"));

        mBottomBar.selectTabAtPosition(activeTabPosition);


        super.onRestoreInstanceState(savedInstanceState);
    }
}
