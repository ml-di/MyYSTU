package ru.ystu.myystu.Activitys;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.UpdateItemsTitle;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Database.Data.CountersData;
import ru.ystu.myystu.Network.LoadLists.GetListEventFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.EventItemsAdapter;
import ru.ystu.myystu.Utils.BellHelper;
import ru.ystu.myystu.Utils.BottomFloatingButton.BottomFloatingButton;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.PaddingHelper;
import ru.ystu.myystu.Utils.SettingsController;

public class EventActivity extends AppCompatActivity {

    private Context mContext;
    private ConstraintLayout mainLayout;
    private String url = "https://www.ystu.ru/events/";                                             // Url страницы событий сайта ЯГТУ
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Parcelable> mList;
    private ArrayList<Parcelable> updateList;
    private Parcelable mRecyclerState;
    private CompositeDisposable mDisposables;
    private GetListEventFromURL getListEventFromURL;
    private AppDatabase db;
    private boolean isUpdate = false;
    private BottomFloatingButton bfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (SettingsController.isDarkTheme(this)) {
            LightStatusBar.setLight(false, false, this, true);
        } else {
            LightStatusBar.setLight(true, true, this, true);
        }
        mContext = this;
        mainLayout = findViewById(R.id.main_layout_event);

        final AppBarLayout appBarLayout = findViewById(R.id.appBar_event);
        final Toolbar mToolbar = findViewById(R.id.toolBar_event);
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
        mRecyclerView = findViewById(R.id.recycler_event_items);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = findViewById(R.id.refresh_event);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorBackgroundTwo));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isUpdate) {
                getEvent(url);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        PaddingHelper.setPaddingStatusBarAndToolBar(mContext, mRecyclerView, true);
        PaddingHelper.setOffsetRefreshLayout(mContext, mSwipeRefreshLayout);
        PaddingHelper.setMarginsAppBar(appBarLayout);

        mDisposables = new CompositeDisposable();
        getListEventFromURL = new GetListEventFromURL();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if (savedInstanceState == null){
            if (getIntent().getExtras() != null){
                isUpdate = getIntent().getExtras().getBoolean("isUpdate", false);
            }
            getEvent(url);
        }
        else {
            isUpdate = savedInstanceState.getBoolean("isUpdate", false);
            mList = savedInstanceState.getParcelableArrayList("mList");
            updateList = savedInstanceState.getParcelableArrayList("updateList");

            if (isUpdate) {
                mRecyclerViewAdapter = new EventItemsAdapter(updateList, this);

                bfb = BottomFloatingButton.onSaveInstance.getBottomFloatingButton();
                bfb.updateFragmentManager(getSupportFragmentManager());
                bfb.setOnClickListener(() -> {
                    isUpdate = false;
                    mRecyclerViewAdapter = new EventItemsAdapter(mList, mContext);
                    mRecyclerViewAdapter.setHasStableIds(true);
                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                    setRecyclerViewAnim(mRecyclerView);
                });
            } else {
                mRecyclerViewAdapter = new EventItemsAdapter(mList, this);
            }

            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        LightStatusBar.setToolBarIconColor(mContext, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_event_openInBrowser) {
            IntentHelper.openInBrowser(this, url);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
        outState.putParcelableArrayList("updateList", updateList);
        outState.putString("url", url);
        outState.putBoolean("isUpdate", isUpdate);

        if (bfb != null) {
            BottomFloatingButton.onSaveInstance.setBottomFloatingButton(bfb);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
        url = savedInstanceState.getString("url");
    }

    public void setUrl (String url)  {
        this.url = url;
    }

    // Загрузка html страницы и ее парсинг
    public void getEvent(String link){

        if(mList == null) {
            mList = new ArrayList<>();
            updateList = new ArrayList<>();
        } else if (!isUpdate) {
            mList.clear();
            updateList.clear();
        }

        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        if(NetworkInformation.hasConnection()){

            final Single<ArrayList<Parcelable>> mSingleEventList
                    = getListEventFromURL.getSingleEventList(link, mList);

            mDisposables.add(mSingleEventList
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ArrayList<Parcelable>>() {

                        @Override
                        public void onSuccess(ArrayList<Parcelable> eventItemsData) {

                            mList = eventItemsData;

                            if(mRecyclerViewAdapter == null) {
                                if (isUpdate) {

                                    AtomicReference<ArrayList<Parcelable>> temp = new AtomicReference<>();
                                    final Thread thread = new Thread(() -> temp.set(getUpdateList(eventItemsData)));
                                    thread.start();

                                    try {
                                        thread.join();
                                        updateList = temp.get();

                                        if (updateList.size() > 1) {
                                            mRecyclerViewAdapter = new EventItemsAdapter(updateList, mContext);
                                        } else {
                                            mRecyclerViewAdapter = new EventItemsAdapter(mList, mContext);
                                            isUpdate = false;
                                        }

                                        mRecyclerViewAdapter.setHasStableIds(true);
                                        mRecyclerView.post(() -> {
                                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                            setRecyclerViewAnim(mRecyclerView);
                                            if (updateList.size() > 1) {
                                                bfb = new BottomFloatingButton(mContext, mainLayout, mContext.getString(R.string.bfb_all_event));
                                                bfb.setIcon(R.drawable.ic_level_back);
                                                bfb.setShowDelay(2000);
                                                bfb.setAnimation(SettingsController.isEnabledAnim(mContext));
                                                bfb.setOnClickListener(() -> {
                                                    isUpdate = false;
                                                    mRecyclerViewAdapter = new EventItemsAdapter(mList, mContext);
                                                    mRecyclerViewAdapter.setHasStableIds(true);
                                                    mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                                    setRecyclerViewAnim(mRecyclerView);
                                                });
                                                bfb.show();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    setUpdateTag(mList);
                                }
                            } else {
                                if (!isUpdate) {
                                    mRecyclerViewAdapter.notifyItemRangeChanged(2, mList.size());
                                }
                            }

                            new Thread(() -> {
                                try {
                                    if (db.getOpenHelper().getWritableDatabase().isOpen()) {
                                        // Удаляем все записи, если они есть
                                        if (db.eventsItemsDao().getCountEventHeader() > 0)
                                            db.eventsItemsDao().deleteEventHeader();
                                        if (db.eventsItemsDao().getCountDividers() > 0)
                                            db.eventsItemsDao().deleteAllDividers();
                                        if (db.eventsItemsDao().getCountEventItems() > 0)
                                            db.eventsItemsDao().deleteAllEventItems();

                                        // Добавляем новые записи
                                        for (Parcelable parcelable : eventItemsData) {
                                            if (parcelable instanceof StringData) {
                                                db.eventsItemsDao().insertDividers((StringData) parcelable);
                                            } else if (parcelable instanceof EventItemsData_Event) {
                                                db.eventsItemsDao().insertEventItems((EventItemsData_Event) parcelable);
                                            } else if (parcelable instanceof EventItemsData_Header) {
                                                db.eventsItemsDao().insertEventHeader((EventItemsData_Header) parcelable);
                                            }
                                        }

                                        // Обновляем счетчики
                                        // Если нет счетчика, создаем
                                        if ( ((EventItemsData_Header) eventItemsData.get(0)).getSelected_id() == 0) {
                                            if (!db.countersDao().isExistsCounter("EVENT")) {
                                                final CountersData countersData = new CountersData();
                                                countersData.setType("EVENT");
                                                countersData.setCount(0);
                                                db.countersDao().insertCounter(countersData);
                                            } else {
                                                db.countersDao().setCount("EVENT", 0);
                                            }
                                        }
                                        runOnUiThread(BellHelper.UpdateListController::updateItems);
                                    }
                                } catch (SQLiteException e) {
                                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                                }
                            }).start();

                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {

                            mSwipeRefreshLayout.setRefreshing(false);

                            if(e.getMessage().equals("Not found")){
                                ErrorMessage.show(mainLayout, 1,
                                        mContext.getResources().getString(R.string.error_message_event_not_found),
                                        mContext);
                            } else {
                                ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                            }
                        }
                    }));
        } else {
            new Thread(() -> {
                try {
                    if (db.getOpenHelper().getReadableDatabase().isOpen() && db.eventsItemsDao().getCountEventItems() > 0) {
                        if (mList.size() > 0)
                            mList.clear();

                        final int countListItems = db.eventsItemsDao().getCountDividers() + db.eventsItemsDao().getCountEventItems() + 2;

                        for (int i = 0; i < countListItems; i++) {
                            if (db.eventsItemsDao().isExistsDivider(i)) {
                                mList.add(db.eventsItemsDao().getDividers(i));
                            } else if (db.eventsItemsDao().isExistsEventItems(i)) {
                                mList.add(db.eventsItemsDao().getEvents(i));
                            } else if (db.eventsItemsDao().isExistsEventHeader(i)) {
                                mList.add(db.eventsItemsDao().getEventHeader(i));
                            }
                        }

                        mRecyclerViewAdapter = new EventItemsAdapter(mList, this);
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
                                                getEvent(url);
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

    private ArrayList<Parcelable> getUpdateList (ArrayList<Parcelable> mList) {

        final ArrayList<Parcelable> tempList = new ArrayList<>();
        tempList.add(new UpdateItemsTitle(getResources().getString(R.string.other_updateFindTitle), R.drawable.ic_update));

        try {
            if (db.getOpenHelper().getReadableDatabase().isOpen() && db.eventsItemsDao().getCountEventItems() > 0) {

                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i) instanceof EventItemsData_Event) {
                        final String link = ((EventItemsData_Event) mList.get(i)).getLink();
                        if (link != null && !db.eventsItemsDao().isExistsEventByLink(link)) {
                            final EventItemsData_Event eventItem = (EventItemsData_Event) mList.get(i);
                            eventItem.setNew(true);
                            tempList.add(eventItem);
                        }
                    }
                }
            }
        } catch (SQLiteException e) {
            runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
        }

        return tempList;
    }

    private void setUpdateTag (ArrayList<Parcelable> mList) {

        final Thread thread = new Thread(() -> {
            try {
                if (db.getOpenHelper().getReadableDatabase().isOpen() && db.eventsItemsDao().getCountEventItems() > 0) {

                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i) instanceof EventItemsData_Event) {
                            final String link = ((EventItemsData_Event) mList.get(i)).getLink();
                            if (link != null && !db.eventsItemsDao().isExistsEventByLink(link)) {
                                ((EventItemsData_Event) mList.get(i)).setNew(true);
                            }
                        }
                    }
                }
            } catch (SQLiteException ignored) { }
        });
        thread.start();

        try {
            thread.join();
            mRecyclerViewAdapter = new EventItemsAdapter(mList, mContext);
            mRecyclerViewAdapter.setHasStableIds(true);
            mRecyclerView.post(() -> {
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                setRecyclerViewAnim(mRecyclerView);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean isRefresh() {
        return mSwipeRefreshLayout.isRefreshing();
    }

}
