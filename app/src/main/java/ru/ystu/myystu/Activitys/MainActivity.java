package ru.ystu.myystu.Activitys;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.AdaptersData.UpdateData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Database.Data.CountersData;
import ru.ystu.myystu.Fragments.BellFragment;
import ru.ystu.myystu.Fragments.MenuFragment;
import ru.ystu.myystu.Fragments.NewsFragment;
import ru.ystu.myystu.Network.UpdateCount.GetCountEvent;
import ru.ystu.myystu.Network.UpdateCount.GetCountJob;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.BottomBarHelper;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

public class MainActivity extends AppCompatActivity {

    private ArrayList<UpdateData> updateList;
    private CoordinatorLayout rootLayout;
    private BottomNavigationView mBottomBar;
    private FragmentManager mFragmentManager;
    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;
    private CoordinatorLayout mContentContainer;
    private int fragmentAnimation;
    private AtomicInteger countUpdate;
    private CompositeDisposable mDisposables;
    private GetCountEvent getCountEvent;
    private GetCountJob getCountJob;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = findViewById(R.id.bottomBar);
        mContentContainer = findViewById(R.id.contentContainer);
        mContentContainer.setFitsSystemWindows(true);
        rootLayout = findViewById(R.id.constraint_main);
        mDisposables = new CompositeDisposable();
        getCountEvent = new GetCountEvent();
        getCountJob = new GetCountJob();

        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        countUpdate = new AtomicInteger();

        if (savedInstanceState == null) {
            updateList = new ArrayList<>();
            mFragmentManager.beginTransaction()
                    .setTransition(fragmentAnimation)
                    .replace(R.id.contentContainer, mNewsFragment, "NEWS_FRAGMENT")
                    .commit();

            LightStatusBar.setLight(true, true, this);
            checkUpdate();
        } else {
            updateList = savedInstanceState.getParcelableArrayList("updateList");
        }

        if(mBottomBar.getSelectedItemId() == R.id.tab_news
                || mBottomBar.getSelectedItemId() == R.id.tab_bell){
            LightStatusBar.setLight(true, true, this);
        } else if (mBottomBar.getSelectedItemId() == R.id.tab_menu){
            LightStatusBar.setLight(false, true, this);
        }

        mBottomBar.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()){
                // Новости
                case R.id.tab_news:

                    mContentContainer.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(fragmentAnimation)
                            .replace(R.id.contentContainer, mNewsFragment, "NEWS_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, true, this);

                    break;
                // Уведомления
                case R.id.tab_bell:
                    mContentContainer.setFitsSystemWindows(true);
                    mFragmentManager.beginTransaction()
                            .setTransition(fragmentAnimation)
                            .replace(R.id.contentContainer, mBellFragment, "BELL_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(true, true, this);

                    break;
                // Меню
                case R.id.tab_menu:
                    mContentContainer.setFitsSystemWindows(false);
                    mContentContainer.setPadding(0, 0, 0, 0);
                    mFragmentManager.beginTransaction()
                            .setTransition(fragmentAnimation)
                            .replace(R.id.contentContainer, mMenuFragment, "MENU_FRAGMENT")
                            .commit();

                    LightStatusBar.setLight(false, true, this);
                    break;
            }

            return true;
        });
        mBottomBar.setOnNavigationItemReselectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.tab_news) {
                ((NewsFragment) mNewsFragment).scrollTopRecyclerView();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Application.getInstance().getDatabase().close();
        if (mDisposables != null)
            mDisposables.dispose();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Подгружаем настройки
        if (SettingsController.isEnabledAnim(this)) {
            fragmentAnimation = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
        } else {
            fragmentAnimation = FragmentTransaction.TRANSIT_NONE;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("selItemId", mBottomBar.getSelectedItemId());
        outState.putInt("countUpdate", countUpdate.get());
        outState.putParcelableArrayList("updateList", updateList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        mBottomBar.setSelectedItemId(savedInstanceState.getInt("selItemId"));
        countUpdate.set(savedInstanceState.getInt("countUpdate"));
        badgeChange(countUpdate.get());

        super.onRestoreInstanceState(savedInstanceState);
    }

    public ArrayList<UpdateData> getUpdateList() {
        return updateList;
    }
    public Fragment getmBellFragment() {
        return mBellFragment;
    }
    public void countUpdateDecrement () {
        countUpdate.getAndDecrement();
    }

    public void badgeChange (int count){

        if(count > 0)
            BottomBarHelper.showBadge(this, mBottomBar, R.id.tab_bell, count);
        else
            BottomBarHelper.removeBadge(this, mBottomBar, R.id.tab_bell);
    }
    public void showBottomBar (boolean isShow) {

        if (mBottomBar != null && mContentContainer != null) {
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
    }

    private void checkUpdate(){

        if (NetworkInformation.hasConnection()) {

            countUpdate.set(0);
            if (updateList.size() > 0) {
                updateList.clear();
            }

            final String urlEvent = "https://www.ystu.ru/events/";
            final String urlJob = "https://www.ystu.ru/information/students/trudoustroystvo/";

            final Single<String> mSingleCountEvent = getCountEvent.getCountEvent(urlEvent);
            final Single<String> mSingleCountJob = getCountJob.getCountJob(urlJob);

            mDisposables.add(Single.merge(mSingleCountEvent, mSingleCountJob)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toList()
                    .subscribe(countersList -> {

                        new Thread(() -> {

                            try {
                                if (db.getOpenHelper().getWritableDatabase().isOpen()) {

                                    for (String s : countersList) {

                                        final String type = s.substring(0, s.indexOf(":"));
                                        final int count = Integer.valueOf(s.substring(s.indexOf(":") + 1));

                                        // Если нет счетчика, создаем
                                        if (!db.countersDao().isExistsCounter(type)) {
                                            final CountersData countersData = new CountersData();
                                            countersData.setType(type);
                                            countersData.setCount(count);
                                            db.countersDao().insertCounter(countersData);
                                        } else {
                                            final int oldCount = db.countersDao().getCountCounter(type);
                                            if (count != oldCount) {
                                                final int countUpdates = Math.abs(count - oldCount);
                                                updateList.add(new UpdateData(type, countUpdates, count));
                                                countUpdate.getAndIncrement();
                                            }
                                        }
                                    }
                                }

                                // Обновляем Bandage
                                runOnUiThread(() -> badgeChange(countUpdate.get()));
                            } catch (SQLiteException e) {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).start();
                    }));
        }
    }
}
