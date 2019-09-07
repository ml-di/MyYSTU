package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.content.Context;
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
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.StoItemsAdapter;
import ru.ystu.myystu.AdaptersData.StoItemsData_Doc;
import ru.ystu.myystu.AdaptersData.StoItemsData_Subtitle;
import ru.ystu.myystu.AdaptersData.StoItemsData_Title;
import ru.ystu.myystu.Application;
import ru.ystu.myystu.Database.AppDatabase;
import ru.ystu.myystu.Network.LoadLists.GetListDocFromURL;
import ru.ystu.myystu.Network.LoadLists.GetListStoFromURL;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.IntentHelper;
import ru.ystu.myystu.Utils.LightStatusBar;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.PaddingHelper;
import ru.ystu.myystu.Utils.SettingsController;

public class StoActivity extends AppCompatActivity {

    private Context mContext;
    private ConstraintLayout mainLayout;
    private final String url_sto_doc = "https://www.ystu.ru/information/students/standart/";
    private final String url_other_doc = "https://www.ystu.ru/information/students/normativnye-dokumenty-po-obucheniyu/";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Parcelable> mList;
    private Parcelable mRecyclerState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable mDisposables;
    private GetListStoFromURL getListStoFromURL;
    private GetListDocFromURL getListDocFromURL;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sto);

        mContext = this;
        mainLayout = findViewById(R.id.main_layout_sto);

        LightStatusBar.setLight(true, true, this, true);

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

        // Отступы сверху
        PaddingHelper.setPaddingStatusBarAndToolBar(mContext, mRecyclerView, true);
        PaddingHelper.setOffsetRefreshLayout(mContext, mSwipeRefreshLayout);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDisposables = new CompositeDisposable();
        getListStoFromURL = new GetListStoFromURL();
        getListDocFromURL = new GetListDocFromURL();

        if (db == null || !db.isOpen())
            db = Application.getInstance().getDatabase();

        if(savedInstanceState == null){
            getSto();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            if (mList != null) {
                mRecyclerViewAdapter = new StoItemsAdapter(mList);
                mRecyclerViewAdapter.setHasStableIds(true);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
            } else {
                getSto();
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sto_openInBrowser) {

            final String[] url_titles = new String[]{getString(R.string.menu_url_titles_sto), getString(R.string.menu_url_titles_doc)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.menu_openInBrowser));
            builder.setItems(url_titles, (dialog, which) -> {
                if (which == 0) {
                    IntentHelper.openInBrowser(mContext, url_sto_doc);
                } else if (which == 1) {
                    IntentHelper.openInBrowser(mContext, url_other_doc);
                }
            });
            builder.setCancelable(true);
            builder.show();
        }

        return true;
    }

    private void getSto() {

        mList = new ArrayList<>();
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        if (NetworkInformation.hasConnection()) {
            final Single<ArrayList<Parcelable>> mSingleStoList = getListStoFromURL.getSingleStoList(url_sto_doc);
            final Single<ArrayList<Parcelable>> mSingleDocList = getListDocFromURL.getSingleDocList(url_other_doc);

            mDisposables.add(Single.concat(mSingleStoList, mSingleDocList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toList()
                    .subscribeWith(new DisposableSingleObserver<List<ArrayList<Parcelable>>>() {

                        @Override
                        public void onSuccess(List<ArrayList<Parcelable>> arrayLists) {
                            mList.addAll(arrayLists.get(0));
                            mList.addAll(arrayLists.get(1));

                            for (int i = 0; i < mList.size(); i++) {
                                if (mList.get(i) instanceof StoItemsData_Title) {
                                    ((StoItemsData_Title) mList.get(i)).setPosition(i);
                                } else if (mList.get(i) instanceof StoItemsData_Subtitle) {
                                    ((StoItemsData_Subtitle) mList.get(i)).setPosition(i);
                                } else if (mList.get(i) instanceof StoItemsData_Doc) {
                                    ((StoItemsData_Doc) mList.get(i)).setPosition(i);
                                }
                            }

                            mRecyclerViewAdapter = new StoItemsAdapter(mList);
                            mRecyclerViewAdapter.setHasStableIds(true);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            setRecyclerViewAnim(mRecyclerView);

                            mSwipeRefreshLayout.setRefreshing(false);

                            new Thread(() -> {
                                try {
                                    if (db.getOpenHelper().getWritableDatabase().isOpen() && mList.size() > 0) {

                                        // Чистим таблицу в БД
                                        if (db.stoItemsDao().getCountTitles() > 0)
                                            db.stoItemsDao().deleteAllTitles();
                                        if (db.stoItemsDao().getCountSubTitles() > 0)
                                            db.stoItemsDao().deleteAllSubTitles();
                                        if (db.stoItemsDao().getCountDocs() > 0)
                                            db.stoItemsDao().deleteAllDoc();

                                        // Добавляем записи
                                        for (Parcelable p : mList) {
                                            if (p instanceof StoItemsData_Title) {
                                                db.stoItemsDao().insertTitles((StoItemsData_Title) p);
                                            } else if (p instanceof StoItemsData_Subtitle) {
                                                db.stoItemsDao().insertSubTitles((StoItemsData_Subtitle) p);
                                            } else if (p instanceof StoItemsData_Doc) {
                                                db.stoItemsDao().insertDoc((StoItemsData_Doc) p);
                                            }
                                        }
                                    }
                                } catch (SQLiteException e) {
                                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                                }
                            }).start();

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
        } else {
            new Thread(() -> {
                try {
                    if (db.getOpenHelper().getReadableDatabase().isOpen() && db.stoItemsDao().getCountDocs() > 0) {
                        if (mList.size() > 0)
                            mList.clear();

                        final int countItems = db.stoItemsDao().getCountTitles()
                                + db.stoItemsDao().getCountSubTitles()
                                + db.stoItemsDao().getCountDocs();

                        for (int i = 0; i < countItems; i++) {
                            if (db.stoItemsDao().isExistsTitle(i)) {
                                mList.add(db.stoItemsDao().getTitle(i));
                            } else if (db.stoItemsDao().isExistsSubTitles(i)) {
                                mList.add(db.stoItemsDao().getSubTitle(i));
                            } else if (db.stoItemsDao().isExistsDoc(i)) {
                                mList.add(db.stoItemsDao().getDoc(i));
                            }
                        }

                        mRecyclerViewAdapter = new StoItemsAdapter(mList);
                        mRecyclerViewAdapter.setHasStableIds(true);
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
                                                getSto();
                                            });

                            ((TextView)snackbar
                                    .getView()
                                    .findViewById(com.google.android.material.R.id.snackbar_text))
                                    .setTextColor(Color.BLACK);

                            PaddingHelper.setMarginsSnackbar(mContext, snackbar);
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

    public void updateItem (int pos) {
        mRecyclerViewAdapter.notifyItemChanged(pos);
    }
}
