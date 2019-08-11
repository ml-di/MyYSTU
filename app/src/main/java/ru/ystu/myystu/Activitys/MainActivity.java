package ru.ystu.myystu.Activitys;
import android.os.Bundle;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Application;
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

    private ArrayList<String> updateList;

    private BottomNavigationView mBottomBar;
    private FragmentManager mFragmentManager;
    private Fragment mNewsFragment;
    private Fragment mBellFragment;
    private Fragment mMenuFragment;
    private CoordinatorLayout mContentContainer;
    private int fragmentAnimation;
    private int countUpdate;
    private CompositeDisposable mDisposables;
    private GetCountEvent getCountEvent;
    private GetCountJob getCountJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomBar = findViewById(R.id.bottomBar);
        mContentContainer = findViewById(R.id.contentContainer);
        mContentContainer.setFitsSystemWindows(true);

        mDisposables = new CompositeDisposable();
        getCountEvent = new GetCountEvent();
        getCountJob = new GetCountJob();

        mFragmentManager = getSupportFragmentManager();

        mNewsFragment = new NewsFragment();
        mBellFragment = new BellFragment();
        mMenuFragment = new MenuFragment();

        if (savedInstanceState == null) {

            updateList = new ArrayList<>();
            mFragmentManager.beginTransaction()
                    .setTransition(fragmentAnimation)
                    .replace(R.id.contentContainer, mNewsFragment, "NEWS_FRAGMENT")
                    .commit();

            LightStatusBar.setLight(true, true, this);
            checkUpdate();
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

                    // TODO передеать аргумент countUpdate

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
            switch (menuItem.getItemId()){
                case R.id.tab_news:
                    ((NewsFragment) mNewsFragment).scrollTopRecyclerView();
                    break;
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
        outState.putInt("countUpdate", countUpdate);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        mBottomBar.setSelectedItemId(savedInstanceState.getInt("selItemId"));
        countUpdate = savedInstanceState.getInt("countUpdate");
        badgeChange(countUpdate);

        super.onRestoreInstanceState(savedInstanceState);
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

            final String urlEvent = "https://www.ystu.ru/events/";
            final String urlJob = "https://www.ystu.ru/information/students/trudoustroystvo/";

            final Single<Integer> mSingleCountEvent = getCountEvent.getCountEvent(urlEvent);
            final Single<Integer> mSingleCountJob = getCountJob.getCountJob(urlJob);

            mDisposables.add(mSingleCountEvent
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));

            mDisposables.add(mSingleCountJob
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }));

        }


        // TODO Получить кол-во обновлений
        countUpdate = 29;
        /*
         *   countUpdate = N;
         * */
        badgeChange(countUpdate);
    }
}
