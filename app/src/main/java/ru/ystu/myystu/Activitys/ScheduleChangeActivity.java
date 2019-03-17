package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.ScheduleChangeAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleChangeData;
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

public class ScheduleChangeActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ScheduleChangeData> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable mDisposables;
    private GetSchedule getSchedule;
    private Context mContext;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_change);

        if(getIntent().getExtras() != null){
            id = getIntent().getExtras().getInt("ID");
        }

        mContext = this;
        mainLayout = findViewById(R.id.main_layout_schedule_change);
        final Toolbar mToolbar = findViewById(R.id.toolBar_schedule_change);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recycler_schdeule_change);
        mSwipeRefreshLayout = findViewById(R.id.refresh_schedule_change);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::showChange);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDisposables = new CompositeDisposable();
        getSchedule = new GetSchedule();

        if(savedInstanceState == null){
            showChange();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new ScheduleChangeAdapter(mList, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mRecyclerState != null)
            mLayoutManager.onRestoreInstanceState(mRecyclerState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposables.dispose();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
    }

    public void showChange(){
        if(NetworkInformation.hasConnection(getApplicationContext())){
            // Изменения
            mList = new ArrayList<>();
            mSwipeRefreshLayout.setRefreshing(true);

            final Single<ArrayList<ScheduleChangeData>> mSingleScheduleChangeList
                    = getSchedule.getChange(id, mList);

            mDisposables.add(mSingleScheduleChangeList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ArrayList<ScheduleChangeData>>() {
                        @Override
                        public void onSuccess(ArrayList<ScheduleChangeData> scheduleChangeData) {
                            mList = scheduleChangeData;
                            try {
                                mRecyclerViewAdapter = new ScheduleChangeAdapter(mList, getApplicationContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                mSwipeRefreshLayout.setRefreshing(false);
                            } finally {
                                dispose();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                            try {
                                if(mRecyclerView == null){
                                    mRecyclerViewAdapter = new ScheduleChangeAdapter(mList, getApplicationContext());
                                    mRecyclerViewAdapter.setHasStableIds(true);
                                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                }

                                if(mSwipeRefreshLayout.isRefreshing())
                                    mSwipeRefreshLayout.setRefreshing(false);

                                if(e.getMessage().equals("Not found")){
                                    ErrorMessage.show(mainLayout, 1,
                                            getResources().getString(R.string.error_message_schedule_change_not_found),
                                            mContext);
                                } else
                                    ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);


                            } finally {
                                dispose();
                            }
                        }
                    }));
        } else {
            // Фрагмент ошибки
            ErrorMessage.show(mainLayout, 0, null, this);
        }
    }
}
