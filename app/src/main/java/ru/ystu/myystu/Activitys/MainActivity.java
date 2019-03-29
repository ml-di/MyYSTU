package ru.ystu.myystu.Activitys;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.ystu.myystu.Fragments.BellFragment;
import ru.ystu.myystu.Fragments.MenuFragment;
import ru.ystu.myystu.Fragments.NewsFragment;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Services.UpdateCheck;
import ru.ystu.myystu.Utils.BottomBarHelper;
import ru.ystu.myystu.Utils.LightStatusBar;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;
    private FragmentManager mFragmentManager;
    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;
    private CoordinatorLayout mContentConteiner;
    private ArrayList<String> updateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateList = new ArrayList<>();
        mBottomBar = findViewById(R.id.bottomBar);
        mContentConteiner = findViewById(R.id.contentConteiner);
        mContentConteiner.setFitsSystemWindows(true);

        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                    .commit();
            LightStatusBar.setLight(true, this);

            // TODO запуск сервиса
            // Запуск сервиса для проверки обновлений
            final PendingIntent mPendingIntent = createPendingResult(1, new Intent(), 0);
            final Intent mIntent = new Intent(this, UpdateCheck.class)
                    .putExtra("pending", mPendingIntent);
            startService(mIntent);
        }

        if(mBottomBar.getSelectedItemId() == R.id.tab_news
                || mBottomBar.getSelectedItemId() == R.id.tab_bell){
            LightStatusBar.setLight(true, this);
        } else if (mBottomBar.getSelectedItemId() == R.id.tab_menu){
            LightStatusBar.setLight(false, this);
        }

        mBottomBar.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                // Новости
                case R.id.tab_news:

                    mContentConteiner.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, this);

                    break;
                // Уведомления
                case R.id.tab_bell:

                    final Bundle bundle = new Bundle();
                    bundle.putStringArrayList("update", updateList);
                    mBellFragment.setArguments(bundle);

                    mContentConteiner.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mBellFragment, "BELL_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, this);

                    break;
                // Меню
                case R.id.tab_menu:
                    mContentConteiner.setFitsSystemWindows(false);
                    mContentConteiner.setPadding(0, 0, 0, 0);
                    mFragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.contentConteiner, mMenuFragment, "MENU_FRAGMENT")
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
        // TODO получаем обновления
        if (data != null) {

            if(data.getStringArrayListExtra("shedule_update") != null){
                if(data.getStringArrayListExtra("shedule_update").size() > 0){
                    for(int i = 0; i < data.getStringArrayListExtra("shedule_update").size(); i++){

                        final String temp = data.getStringArrayListExtra("shedule_update").get(i);
                        if(!updateList.contains(temp)){
                            updateList.add(temp);
                        }
                    }
                }
            }

            badgeChange(updateList.size());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putStringArrayList("updateList", updateList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        updateList = savedInstanceState.getStringArrayList("updateList");
        if (updateList != null && updateList.size() > 0) {
            badgeChange(updateList.size());
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void removeItemUpdate (int position){
        updateList.remove(position);
    }
    public void badgeChange (int count){

        if(count > 0)
            BottomBarHelper.showBadge(this, mBottomBar, R.id.tab_bell, count);
        else
            BottomBarHelper.removeBadge(this, mBottomBar, R.id.tab_bell);
    }
    public void showBottomBar (boolean isShow) {

        final CoordinatorLayout.LayoutParams paramsBottomBar = (CoordinatorLayout.LayoutParams) mBottomBar.getLayoutParams();
        final CoordinatorLayout.LayoutParams paramsMainLayout = (CoordinatorLayout.LayoutParams) mContentConteiner.getLayoutParams();
        if(isShow){
            paramsBottomBar.setBehavior(null);
            paramsMainLayout.setMargins(0, 0, 0, mBottomBar.getHeight());
        } else {
            paramsBottomBar.setBehavior(new HideBottomViewOnScrollBehavior());
            paramsMainLayout.setMargins(0, 0, 0, 0);
        }

    }
}
