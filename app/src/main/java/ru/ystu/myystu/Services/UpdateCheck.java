package ru.ystu.myystu.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.UpdateService;

public class UpdateCheck extends Service {

    private UpdateService mUpdateService;
    private CompositeDisposable mDisposables;
    private Context mContext;
    private PendingIntent mPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mUpdateService = new UpdateService(this);
        mDisposables = new CompositeDisposable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPendingIntent = intent.getParcelableExtra("pending");
        update();
        // TODO не понятно пререзапускается ли
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDisposables.dispose();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void update(){

        final List<String> links = new ArrayList<>();
        final Observable<String> mObservable = mUpdateService.checkShedule();
        final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

        mDisposables.add(mObservable
                .subscribeOn(Schedulers.io())
                .delay(10, TimeUnit.SECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {

                        if(!links.contains(s)){
                            links.add(s);
                            onComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        final Intent intent = new Intent().putExtra("shedule_update", links.get(links.size() - 1));
                        try {
                            mPendingIntent.send(UpdateCheck.this, 0, intent);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }
}
