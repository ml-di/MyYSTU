package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Adapters.ScheduleItemAdapter;
import ru.ystu.myystu.AdaptersData.ScheduleListItemData;
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.ErrorMessage;
import ru.ystu.myystu.Utils.FileInformation;
import ru.ystu.myystu.Utils.NetworkInformation;
import ru.ystu.myystu.Utils.ZipUtils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private ZipUtils zipUtils;
    private int id;
    private String link;
    private final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};
    private File dir = new File(Environment.getExternalStorageDirectory(), "/.MyYSTU");
    private ArrayList<ScheduleListItemData> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        mContext = this;
        mainLayout = findViewById(R.id.main_layout_schedule_list);

        final Toolbar mToolBar = findViewById(R.id.toolBar_scheduleList);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolBar.setNavigationOnClickListener(view -> onBackPressed());

        mRecyclerView = findViewById(R.id.recycler_schdeule_items);
        mSwipeRefreshLayout = findViewById(R.id.refresh_schedule);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::getSchedule);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mDisposables = new CompositeDisposable();
        getSchedule = new GetSchedule();
        zipUtils = new ZipUtils();

        id = getIntent().getIntExtra("ID", 0);

        if(savedInstanceState == null){
            getSchedule();
        } else{
            mList = savedInstanceState.getParcelableArrayList("mList");
            mRecyclerViewAdapter = new ScheduleItemAdapter(mList, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposables.dispose();
    }

    private void getSchedule(){

        mList = new ArrayList<>();

        mSwipeRefreshLayout.setRefreshing(true);

        if(NetworkInformation.hasConnection(this)){

            final Single<String> mSingle = getSchedule.getLink(id);
            mDisposables.add(mSingle
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<String>() {

                        @Override
                        public void onSuccess(String s) {
                            link = s;

                            if(getSchedule.isNew(id, link, getApplicationContext()))
                                openSchedule(true, link);
                            else
                                openSchedule(false, link);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if(e.getMessage().equals("Not found")){
                                final File file = new File(dir, prefix[id] + ".zip");
                                if(file.exists()){
                                    openFile();
                                    Toast.makeText(mContext,
                                            getResources().getString(R.string.error_message_shedule_not_update),
                                            Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    ErrorMessage.show(mainLayout, 1,
                                            getResources().getString(R.string.error_message_schedule_file_not_found),
                                            mContext);
                                }
                            } else
                            ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        }
                    }));

        } else
            openSchedule(false, null);
    }

    private void openSchedule (boolean isDownload, String link){

        // Скачать и открыть расписание
        if(isDownload && link != null){
            downloadFile();
        } else {
            final File file = new File(dir, prefix[id] + ".zip");
            if(file.exists())
                openFile();
            else if(link != null)
                downloadFile();
            else{
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorMessage.show(mainLayout, 0, null, this);
            }
        }
    }

    private void downloadFile(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {

            if(createTempDir(dir)){
                final Completable mCompletable = getSchedule.downloadSchedule(link, id, this, dir.getAbsolutePath());
                mDisposables.add(mCompletable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {

                            @Override
                            public void onError(Throwable e) {

                                mSwipeRefreshLayout.setRefreshing(false);
                                ErrorMessage.show(mainLayout, -1,
                                        getResources().getString(R.string.error_message_schedule_network_download_error),
                                        mContext);
                            }

                            @Override
                            public void onComplete() {

                                SharedPreferences mSharedPreferences = getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE);
                                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                                mEditor.putString(prefix[id].toUpperCase(), link);
                                mEditor.apply();
                                mSharedPreferences = getSharedPreferences("SCHEDULE_UPDATE", Context.MODE_PRIVATE);
                                mEditor = mSharedPreferences.edit();
                                mEditor.putString(prefix[id].toUpperCase(), link);
                                mEditor.apply();
                                openFile();

                            }
                        }));
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                ErrorMessage.show(mainLayout, -1,
                        getResources().getString(R.string.error_message_schedule_dir_create_error),
                        mContext);
            }
        }
    }

    private void openFile(){
        final File s = new File(dir, prefix[id] + ".zip");
        if(s.exists()){

            final FileInformation mFileInformation = new FileInformation();
            final int[] index = {0};

            final Observable<String> unzipObservable = zipUtils.unzipFile(s, new File(dir + "/" + prefix[id]));
            mDisposables.add(unzipObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>(){
                        @Override
                        public void onNext(String s) {

                            String fileName = s.substring(0, s.lastIndexOf("."));

                            final String fileType = s.substring(s.lastIndexOf(".") + 1, s.lastIndexOf(":"));
                            final long size = Long.parseLong(s.substring(s.lastIndexOf(":") + 1, s.lastIndexOf("*")));
                            final String fileSize = mFileInformation.getFileSize(size);
                            final String fileModifyDate = s.substring(s.lastIndexOf("*") + 1);

                            mList.add(new ScheduleListItemData(index[0], id, fileName, fileSize, fileType, fileModifyDate));
                            index[0]++;
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            ErrorMessage.show(mainLayout, -1, e.getMessage(), mContext);
                        }

                        @Override
                        public void onComplete() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mRecyclerViewAdapter = new ScheduleItemAdapter(mList, getApplicationContext());
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        }
                    }));
        } else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private static boolean createTempDir (File dir){

        if(!dir.exists()){
            return dir.mkdirs();
        } else
            return true;

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
        getMenuInflater().inflate(R.menu.menu_schedule_open, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_schedule_open_change){
            final Intent mIntent = new Intent(this, ScheduleChangeActivity.class);
            mIntent.putExtra("ID", id);
            startActivity(mIntent);
        }

        return true;
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
