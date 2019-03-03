package ru.ystu.myystu.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.OlympItemsAdapter;
import ru.ystu.myystu.Network.GetListJobFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.JobItemsAdapter;
import ru.ystu.myystu.AdaptersData.JobItemsData;

public class JobActivity extends AppCompatActivity {

    private final String url = "https://www.ystu.ru/learning/placement/"; // Url страницы трудоустройство сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<JobItemsData> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable disposables;
    private GetListJobFromURL getListJobFromURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        final Toolbar toolbar = findViewById(R.id.toolBar_job);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setOnClickListener(e -> {
            if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0 && mRecyclerView != null){
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 10)
                    mRecyclerView.smoothScrollToPosition(0);
                else
                    mRecyclerView.scrollToPosition(0);

            }
        });

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recycler_job_items);
        mSwipeRefreshLayout = findViewById(R.id.refresh_job);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::getJob);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        disposables = new CompositeDisposable();
        getListJobFromURL = new GetListJobFromURL();

        if(savedInstanceState == null){
            getJob();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new JobItemsAdapter(mList, this);
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

        disposables.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_job_openInBrowser) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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

    // Загрузка html страницы и ее парсинг
    private void getJob(){

        mList = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);

        final Observable<ArrayList<JobItemsData>> observableJobList
                = getListJobFromURL.getObservableJobList(url, mList);
        disposables.add(observableJobList
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<JobItemsData>>() {
                    @Override
                    public void onNext(ArrayList<JobItemsData> jobItemsData) {
                        mList = jobItemsData;
                    }

                    @Override
                    public void onError(Throwable e) {

                        try{

                            if(mRecyclerViewAdapter == null){
                                mRecyclerViewAdapter = new JobItemsAdapter(mList, getApplicationContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            }

                            if(mSwipeRefreshLayout.isRefreshing())
                                mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(JobActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } finally {
                            dispose();
                        }
                    }

                    @Override
                    public void onComplete() {

                        try {
                            mRecyclerViewAdapter = new JobItemsAdapter(mList, getApplicationContext());
                            mRecyclerViewAdapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            mSwipeRefreshLayout.setRefreshing(false);
                        } finally {
                            dispose();
                        }
                    }
                }));
    }
}
