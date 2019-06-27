package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Database.Data.ScheduleChangeBDData;
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

        LightStatusBar.setLight(true, this);
        mContext = this;
        mainLayout = findViewById(R.id.main_layout_schedule_list);

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
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::getSchedule);
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_schedule_items);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

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
        if (db != null && db.isOpen())
            db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()) {
            overridePendingTransition(R.anim.activity_slide_right_show_reverse, R.anim.activity_slide_left_out_reverse);
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

        mList.add(new ToolbarPlaceholderData(0));
        mSwipeRefreshLayout.setRefreshing(true);

        if(NetworkInformation.hasConnection(this)){

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

                                // Добавляем в БД
                                try {
                                    new Thread(() -> {
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

                                    }).start();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }


                            } else {
                                ErrorMessage.show(mainLayout, 1,
                                        getResources().getString(R.string.error_message_schedule_file_not_found),
                                        mContext);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        getResources().getString(R.string.error_message_schedule_file_not_found),
                                        mContext);
                            } else
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        }
                    }));

        } else {

            try {
                new Thread(() -> {
                    if (db.scheduleItemDao().getCountScheduleList(id) > 0) {
                        if (mList.size() > 0)
                            mList.clear();

                        mList.add(new ToolbarPlaceholderData(0));
                        mList.addAll(db.scheduleItemDao().getScheduleList(id));

                        mRecyclerViewAdapter = new ScheduleItemAdapter(mList, this);
                        mRecyclerView.post(() -> {
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
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
                                    .setTextColor(Color.BLACK);

                            snackbar.show();

                            mSwipeRefreshLayout.setRefreshing(false);
                        });

                    } else {
                        runOnUiThread(() -> {
                            ErrorMessage.show(mainLayout, 0, null, mContext);
                            mSwipeRefreshLayout.setRefreshing(false);
                        });
                    }
                }).start();

                new Thread(() -> {
                    if (db.scheduleItemDao().getCountScheduleChange(id) > 0) {
                        if (changeList.size() > 0)
                            changeList.clear();

                        for (int i = 0; i < db.scheduleItemDao().getCountScheduleChange(id); i++) {
                            changeList.add(db.scheduleItemDao().getScheduleChange(id).get(i).getText());
                        }
                    }
                }).start();

            } catch (Exception e) {
                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                mSwipeRefreshLayout.setRefreshing(false);
            }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_schedule_open_change){
            final Intent mIntent = new Intent(this, ScheduleChangeActivity.class);
            mIntent.putExtra("mList", changeList);
            startActivity(mIntent);
            overridePendingTransition(R.anim.activity_slide_right_show, R.anim.activity_slide_left_out);

        }

        return true;
    }

    public void updateItem (int position) {
        mRecyclerViewAdapter.notifyItemChanged(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Разрешение успешно получено, повторите действие", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
