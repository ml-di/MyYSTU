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
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
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

        mDisposables.dispose();
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
                                mList.add(new ScheduleListItemData(id, name, link));
                            }
                        }

                        @Override
                        public void onComplete() {

                            if (mList.size() > 1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                mRecyclerViewAdapter = new ScheduleItemAdapter(mList, getApplicationContext());
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
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

            final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};
            final File dir = new File(Environment.getExternalStorageDirectory(),
                    "/.MyYSTU/" + prefix[id]);

            if(dir.list() != null) {
                for (String name : dir.list()) {
                    mList.add(new ScheduleListItemData(id, name.substring(0, name.lastIndexOf(".")), "http://www.ystu.ru/" + name));
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mRecyclerViewAdapter = new ScheduleItemAdapter(mList, getApplicationContext());
                mRecyclerView.setAdapter(mRecyclerViewAdapter);

            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorMessage.show(mainLayout, 0, null, this);
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
