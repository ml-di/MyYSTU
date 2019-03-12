package ru.ystu.myystu.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.GetListOlympFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.OlympItemsAdapter;
import ru.ystu.myystu.AdaptersData.OlympItemsData;

public class OlympActivity extends AppCompatActivity {

    private String url = "https://www.ystu.ru/science/olimp/"; // Url страницы олимпиады сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<OlympItemsData> mList;
    private Parcelable mRecyclerState;
    private CompositeDisposable mDisposables;
    private GetListOlympFromURL getListOlympFromURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olymp);

        final Toolbar mToolbar = findViewById(R.id.toolBar_olymp);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnClickListener(e -> {
            if(mRecyclerView != null){
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 3)
                    mRecyclerView.smoothScrollToPosition(0);
                else
                    mRecyclerView.scrollToPosition(0);

            }
        });

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recycler_olymp_items);
        mSwipeRefreshLayout = findViewById(R.id.refresh_olymp);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::getOlymp);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mDisposables = new CompositeDisposable();
        getListOlympFromURL = new GetListOlympFromURL();

        if(savedInstanceState == null){
            getOlymp();
        }
        else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new OlympItemsAdapter(mList, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposables.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_olymp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_olymp_openInBrowser) {
            final Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(mIntent);
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
    private void getOlymp(){
        mList = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);

        final Single<ArrayList<OlympItemsData>> mSingleOlympList
                = getListOlympFromURL.getSingleOlympList(url, mList);

        mDisposables.add(mSingleOlympList
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ArrayList<OlympItemsData>>() {

                    @Override
                    public void onSuccess(ArrayList<OlympItemsData> olympItemsData) {
                        mList = olympItemsData;
                        try {
                            mRecyclerViewAdapter = new OlympItemsAdapter(mList, getApplicationContext());
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
                                mRecyclerViewAdapter = new OlympItemsAdapter(mList, getApplicationContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            }

                            if(mSwipeRefreshLayout.isRefreshing())
                                mSwipeRefreshLayout.setRefreshing(false);

                            Toast.makeText(OlympActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        } finally {
                            dispose();
                        }

                    }
                }));
    }
}
