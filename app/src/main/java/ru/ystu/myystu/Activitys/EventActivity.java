package ru.ystu.myystu.Activitys;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.snackbar.Snackbar;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.AdaptersData.EventItemsData_Event;
import ru.ystu.myystu.AdaptersData.EventItemsData_Header;
import ru.ystu.myystu.AdaptersData.StringData;
import ru.ystu.myystu.AdaptersData.ToolbarPlaceholderData;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Network.LoadLists.GetListEventFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Adapters.EventItemsAdapter;
import ru.ystu.myystu.Utils.Converter;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
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
    private Parcelable mRecyclerState;
    private CompositeDisposable mDisposables;
    private GetListEventFromURL getListEventFromURL;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        LightStatusBar.setLight(true, true, this);
        mContext = this;
        mainLayout = findViewById(R.id.main_layout_event);

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
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getEvent(url));
        mSwipeRefreshLayout.setProgressViewOffset(true, 0, (int) Converter.convertDpToPixel(70, mContext));

        mDisposables = new CompositeDisposable();
        getListEventFromURL = new GetListEventFromURL();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if(savedInstanceState == null){
            getEvent(url);
        }
        else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new EventItemsAdapter(mList, this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_event_openInBrowser) {
            final Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(mIntent);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerState);
        outState.putParcelableArrayList("mList", mList);
        outState.putString("url", url);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable("recyclerViewState");
        url = savedInstanceState.getString("url");
    }

    public void setUrl (String url) {
        this.url = url;
    }

    // Загрузка html страницы и ее парсинг
    public void getEvent(String link){

        if(mList == null)
            mList = new ArrayList<>();
        else
            mList.clear();

        mSwipeRefreshLayout.setRefreshing(true);

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
                                mRecyclerViewAdapter = new EventItemsAdapter(mList, getApplicationContext());
                                mRecyclerViewAdapter.setHasStableIds(true);
                                mRecyclerView.setAdapter(mRecyclerViewAdapter);
                                setRecyclerViewAnim(mRecyclerView);
                            } else {
                                mRecyclerViewAdapter.notifyItemRangeChanged(2, mList.size());
                                setRecyclerViewAnim(mRecyclerView);
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

                        mList.add(new ToolbarPlaceholderData(0));

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
}
