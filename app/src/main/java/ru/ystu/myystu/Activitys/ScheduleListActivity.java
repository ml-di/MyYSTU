package ru.ystu.myystu.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.GetSchedule;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.NetworkInformation;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class ScheduleListActivity extends AppCompatActivity {

    private CompositeDisposable mDisposables;
    private GetSchedule getSchedule;
    private int id;
    private String link;
    final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        mDisposables = new CompositeDisposable();
        getSchedule = new GetSchedule();

        id = getIntent().getIntExtra("ID", 0);

        if(NetworkInformation.hasConnection(this)){

            final Observable<String> mObservable = getSchedule.getLink(id);
            mDisposables.add(mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String s) {
                            link = s;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(ScheduleListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                            if(getSchedule.isNew(id, link, getApplicationContext()))
                                openSchedule(true, link);
                            else
                                openSchedule(false, link);
                        }
                    }));

        } else
            openSchedule(false, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisposables.dispose();
    }

    private void openSchedule (boolean isDownload, String link){

        Toast.makeText(this, isDownload + " " + link, Toast.LENGTH_SHORT).show();

        // Скачать и открыть расписание
        if(isDownload && link != null){
            downloadFile();
        } else {
            final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            final File file = new File(dir, prefix[id] + ".zip");
            if(file.exists())
                openFile();
            else if(link != null)
                downloadFile();
            else
                Toast.makeText(this, getResources().getString(R.string.schedule_network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {

            final boolean[] isComplete = {false};

            final Observable<Boolean> mObservable = getSchedule.downloadSchedule(link, id, this);
            mDisposables.add(mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Boolean>() {

                        @Override
                        public void onNext(Boolean v) {
                            isComplete[0] = v;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(ScheduleListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                            // Успешно скачался
                            if(isComplete[0]){
                                final SharedPreferences mSharedPreferences = getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE);
                                final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                                mEditor.putString(prefix[id].toUpperCase(), link);
                                mEditor.apply();
                                openFile();
                            } else {
                                Toast.makeText(ScheduleListActivity.this,
                                        getResources().getString(R.string.schedule_network_download_error),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }));
        }
    }

    private void openFile(){
        final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(dir, prefix[id] + ".zip");
        if(file.exists()){

        }
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
