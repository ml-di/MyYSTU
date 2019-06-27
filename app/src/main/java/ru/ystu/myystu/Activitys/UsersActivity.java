package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.UsersItemsAdapter;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.AdaptersData.UsersItemsData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Network.GetListUsersFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private Context mContext;
    private ConstraintLayout mainLayout;
    private final String url = "https://www.ystu.ru/users/";                                        // Url страницы сотрудников и преподавателей ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Parcelable> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable mDisposables;
    private GetListUsersFromURL getListUsersFromURL;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mContext = this;
        mainLayout = findViewById(R.id.main_layout_users);

        LightStatusBar.setLight(true, this);

        final Toolbar mToolbar = findViewById(R.id.toolBar_users);
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

        mRecyclerView = findViewById(R.id.recycler_users_items);
        mSwipeRefreshLayout = findViewById(R.id.refresh_users);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(this::getUsers);
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mDisposables = new CompositeDisposable();

        getListUsersFromURL = new GetListUsersFromURL();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if(savedInstanceState == null){
            getUsers();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new UsersItemsAdapter(mList, this);
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

        if (mDisposables != null)
            mDisposables.dispose();

        if (db != null && db.isOpen())
            db.close();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

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
        getMenuInflater().inflate(R.menu.menu_user, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_user_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText) && mRecyclerViewAdapter != null) {
                    ((UsersItemsAdapter) mRecyclerViewAdapter).getFilter().filter("");
                } else {
                    if (mRecyclerViewAdapter != null)
                        ((UsersItemsAdapter) mRecyclerViewAdapter).getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    // Получение списка сотрудников и преподователей
    private void getUsers() {

        mList = new ArrayList<>();
        mSwipeRefreshLayout.setRefreshing(true);

        if (NetworkInformation.hasConnection(mContext)) {

            final Single<List<Parcelable>> mSingleUsersList
                    = getListUsersFromURL.getSingleUsersList(url, mList);

            mDisposables.add(mSingleUsersList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Parcelable>>() {
                        @Override
                        public void onSuccess(List<Parcelable> usersItemsData) {

                            mList = usersItemsData;

                            if (mRecyclerViewAdapter == null) {
                                mRecyclerViewAdapter = new UsersItemsAdapter(mList, getApplicationContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.swapAdapter(mRecyclerViewAdapter, true);
                            } else {
                                mRecyclerViewAdapter.notifyItemRangeChanged(1, mList.size());
                            }

                            try {
                                new Thread(() -> {
                                    // Удаляем все записи, если они есть
                                    if (db.usersItemsDao().getCount() > 0) {
                                        db.usersItemsDao().deleteAll();
                                    }

                                    // Добавляем новые записи
                                    for (Parcelable parcelable : usersItemsData) {
                                        if (parcelable instanceof UsersItemsData) {
                                            db.usersItemsDao().insert((UsersItemsData) parcelable);
                                        }
                                    }
                                }).start();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {

                            mSwipeRefreshLayout.setRefreshing(false);

                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        mContext.getResources().getString(R.string.error_message_job_not_found),
                                        mContext);
                            } else {
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                            }
                        }
                    }));
        } else {

            try {
                new Thread(() -> {
                    if (db.usersItemsDao().getCount() > 0) {
                        if (mList.size() > 0)
                            mList.clear();

                        mList.add(new ToolbarPlaceholderData(0));
                        mList.addAll(db.usersItemsDao().getAllUsersItems());

                        mRecyclerViewAdapter = new UsersItemsAdapter(mList, this);
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
                                                getUsers();
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
            } catch (Exception e) {
                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
