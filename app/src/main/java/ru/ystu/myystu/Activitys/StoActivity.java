package ru.ystu.myystu.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.StoItemsAdapter;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Network.LoadLists.GetListStoFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.SettingsController;

public class StoActivity extends AppCompatActivity {

    private Context mContext;
    private ConstraintLayout mainLayout;
    private final String url = "https://www.ystu.ru/information/students/standart/";         // Url страницы трудоустройство сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Parcelable> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable mDisposables;
    private GetListStoFromURL getListStoFromURL;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sto);

        mContext = this;
        mainLayout = findViewById(R.id.main_layout_sto);

        LightStatusBar.setLight(true, true, this);

        final Toolbar mToolbar = findViewById(R.id.toolBar_sto);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnClickListener(e -> {
            if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() > 0 && mRecyclerView != null){
                if(((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition() < 10)
                    mRecyclerView.smoothScrollToPosition(0);
                else
                    mRecyclerView.scrollToPosition(0);

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_sto_items);
        mSwipeRefreshLayout = findViewById(R.id.refresh_sto);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::getSto);
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDisposables = new CompositeDisposable();
        getListStoFromURL = new GetListStoFromURL();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if(savedInstanceState == null){
            getSto();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new StoItemsAdapter(mList);
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
    protected void onPause() {
        super.onPause();

        if (isFinishing() && !SettingsController.isEnabledAnim(this)) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDisposables != null)
            mDisposables.dispose();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", (ArrayList<? extends Parcelable>) mList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sto_openInBrowser) {
            IntentHelper.openInBrowser(mContext, url);
        }

        return true;
    }

    private void getSto() {

        mList = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);

        if (NetworkInformation.hasConnection()) {
            final Single<ArrayList<Parcelable>> mSingleStoList = getListStoFromURL.getSingleStoList(url, mList);

            mDisposables.add(mSingleStoList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ArrayList<Parcelable>>() {
                        @Override
                        public void onSuccess(ArrayList<Parcelable> parcelables) {

                            mList = parcelables;
                            mRecyclerViewAdapter = new StoItemsAdapter(mList);
                            mRecyclerViewAdapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            setRecyclerViewAnim(mRecyclerView);

                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);

                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        mContext.getResources().getString(R.string.error_message_sto_not_found_post),
                                        mContext);
                            } else {
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                            }
                        }
                    }));
        }
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
