package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.ScheduleItemAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Database.Data.ScheduleChangeBDData;
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.PaddingHelper;
import ru.ystu.myystu.Utils.SettingsController;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ScheduleListActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private Context mContext;
    private CompositeDisposable mDisposables;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GetSchedule getSchedule;
    private Parcelable mRecyclerState;
    private int id;
    private ArrayList<Parcelable> mList;
    private ArrayList<String> changeList;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        if (SettingsController.isDarkTheme(this)) {
            LightStatusBar.setLight(false, false, this, true);
        } else {
            LightStatusBar.setLight(true, true, this, true);
        }
        mContext = this;
        mainLayout = findViewById(R.id.main_layout_schedule_list);

        final AppBarLayout appBarLayout = findViewById(R.id.appBar_schedule_list);
        final Toolbar mToolBar = findViewById(R.id.toolBar_scheduleList);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolBar.setNavigationOnClickListener(view -> onBackPressed());
        mToolBar.setOnClickListener(e -> {
            if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0 && mRecyclerView != null){
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 10)
                    mRecyclerView.smoothScrollToPosition(0);
                else
                    mRecyclerView.scrollToPosition(0);
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.refresh_schedule);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorBackgroundTwo));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::getSchedule);
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_schedule_items);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        PaddingHelper.setPaddingStatusBarAndToolBar(mContext, mRecyclerView, true);
        PaddingHelper.setOffsetRefreshLayout(mContext, mSwipeRefreshLayout);
        PaddingHelper.setMarginsAppBar(appBarLayout);

        mDisposables = new CompositeDisposable();
        getSchedule = new GetSchedule();

        id = getIntent().getIntExtra("ID", 0);

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if(savedInstanceState == null){
            getSchedule();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            changeList = savedInstanceState.getStringArrayList("cList");
            mRecyclerViewAdapter = new ScheduleItemAdapter(mList, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposables != null)
            mDisposables.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()) {
            if (SettingsController.isEnabledAnim(this)) {
                overridePendingTransition(R.anim.activity_slide_right_show_reverse, R.anim.activity_slide_left_out_reverse);
            } else {
                overridePendingTransition(0, 0);
            }
        }
    }

    private void getSchedule(){

        if(mList == null){
            mList = new ArrayList<>();
            changeList = new ArrayList<>();
        } else {
            mList.clear();
            changeList.clear();
        }

        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        if(NetworkInformation.hasConnection()){

            final int[] index = {id * 100};
            final Observable<String> mObservable = getSchedule.getLink(id);
            mDisposables.add(mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String s) {
                            if(s.startsWith("change:")){
                                changeList.add(s.substring(s.indexOf(":") + 1));
                            } else if (s.startsWith("links")) {
                                final String link = s.substring(s.indexOf(":") + 1, s.lastIndexOf("*"));
                                final String name = s.substring(s.lastIndexOf("*") + 1);
                                mList.add(new ScheduleListItemData(index[0], id, name, link));
                                index[0]++;
                            }
                        }

                        @Override
                        public void onComplete() {

                            if (mList.size() > 1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                mRecyclerViewAdapter = new ScheduleItemAdapter(mList, getApplicationContext());
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                setRecyclerViewAnim(mRecyclerView);

                                // Добавляем в БД
                                new Thread(() -> {
                                    try {
                                        if (db.getOpenHelper().getWritableDatabase().isOpen()) {
                                            // Удаляем все записи, если они есть
                                            if (db.scheduleItemDao().getCountScheduleList(id) > 0) {
                                                db.scheduleItemDao().deleteList(id);
                                            }

                                            if (db.scheduleItemDao().getCountScheduleChange(id) > 0) {
                                                db.scheduleItemDao().deleteChange(id);
                                            }

                                            // Добавляем новые записи с расписанием
                                            for (Parcelable parcelable : mList) {
                                                if (parcelable instanceof ScheduleListItemData) {
                                                    db.scheduleItemDao().insertList((ScheduleListItemData) parcelable);
                                                }
                                            }
                                            // Добавляем новые записи с изменениями
                                            int index = id * 100;
                                            for (String s : changeList) {
                                                db.scheduleItemDao().insertChange(new ScheduleChangeBDData(index, id, s));
                                                index++;
                                            }
                                        }
                                    } catch (SQLiteException e) {
                                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                                    }
                                }).start();
                            } else {
                                ErrorMessage.show(mainLayout, 1,
                                        getResources().getString(R.string.error_message_file_not_found),
                                        mContext);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        getResources().getString(R.string.error_message_file_not_found),
                                        mContext);
                            } else
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        }
                    }));

        } else {
            // Подгрузка списка файлов с расписанием
            new Thread(() -> {
                try {
                    if (db.getOpenHelper().getReadableDatabase().isOpen() && db.scheduleItemDao().getCountScheduleList(id) > 0) {
                        if (mList.size() > 0)
                            mList.clear();

                        mList.addAll(db.scheduleItemDao().getScheduleList(id));

                        mRecyclerViewAdapter = new ScheduleItemAdapter(mList, this);
                        mRecyclerView.post(() -> {
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            setRecyclerViewAnim(mRecyclerView);
                            // SnackBar с предупреждением об отсутствие интернета
                            final Snackbar snackbar = Snackbar
                                    .make(
                                            mainLayout,
                                            getResources().getString(R.string.toast_no_connection_the_internet),
                                            Snackbar.LENGTH_INDEFINITE)
                                    .setAction(
                                            getResources().getString(R.string.error_message_refresh),
                                            view -> {
                                                // Обновление данных
                                                getSchedule();
                                            });

                            ((TextView)snackbar
                                    .getView()
                                    .findViewById(com.google.android.material.R.id.snackbar_text))
                                    .setTextColor(getResources().getColor(R.color.colorTextBlack));

                            snackbar.show();

                            mSwipeRefreshLayout.setRefreshing(false);
                        });
                    } else {
                        runOnUiThread(() -> {
                            ErrorMessage.show(mainLayout, 0, null, mContext);
                            mSwipeRefreshLayout.setRefreshing(false);
                        });
                    }
                } catch (SQLiteException e) {
                    runOnUiThread(() -> {
                        ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        mSwipeRefreshLayout.setRefreshing(false);
                    });
                }
            }).start();

            // Подгрузка изменений в расписании
            new Thread(() -> {
                try {
                    if (db.getOpenHelper().getReadableDatabase().isOpen() && db.scheduleItemDao().getCountScheduleChange(id) > 0) {
                        if (changeList.size() > 0)
                            changeList.clear();

                        for (int i = 0; i < db.scheduleItemDao().getCountScheduleChange(id); i++) {
                            changeList.add(db.scheduleItemDao().getScheduleChange(id).get(i).getText());
                        }
                    }
                } catch (SQLiteException e) {
                    runOnUiThread(() -> {
                        ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        mSwipeRefreshLayout.setRefreshing(false);
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
        outState.putStringArrayList("cList", changeList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule_open, menu);
        LightStatusBar.setToolBarIconColor(mContext, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_schedule_open_change){
            final Intent mIntent = new Intent(this, ScheduleChangeActivity.class);
            mIntent.putExtra("mList", changeList);
            startActivity(mIntent);
            if (SettingsController.isEnabledAnim(this)) {
                overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);
            } else {
                overridePendingTransition(0, 0);
            }
        }

        return true;
    }

    public void updateItem (int position) {
        mRecyclerViewAdapter.notifyItemChanged(position);
    }

    private void setRecyclerViewAnim (final RecyclerView recyclerView) {
        if (SettingsController.isEnabledAnim(this)) {
            final Context context = recyclerView.getContext();
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_main_recyclerview_show);
            recyclerView.setLayoutAnimation(controller);
        } else {
            recyclerView.clearAnimation();
        }
    }
}
