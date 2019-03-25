package ru.ystu.myystu.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.UpdateService;

public class UpdateCheck extends Service {

    // TODO интервал обновления
    private final int DELAY_UPDATE_SEC = 3600;

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

        // TODO Update
        final ArrayList<String> links = new ArrayList<>();
        final Observable<String> mObservable = mUpdateService.checkShedule();
        final boolean[] isUpdate = {false};

        mDisposables.add(mObservable
                .subscribeOn(Schedulers.io())
                .repeatWhen(objectObservable -> objectObservable.delay(DELAY_UPDATE_SEC, TimeUnit.SECONDS))
                .retryWhen(throwableObservable -> throwableObservable.delay(DELAY_UPDATE_SEC, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {

                        if(s.equals("end")){
                            onComplete();
                        } else if(!links.contains(s)){
                            links.add(s);
                            isUpdate[0] = true;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        if(links.size() > 0 && isUpdate[0]){

                            final Intent intent = new Intent();
                            intent.putStringArrayListExtra("shedule_update", links);

                            try {
                                mPendingIntent.send(UpdateCheck.this, 0, intent);
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }

                            isUpdate[0] = false;
                        }
                    }
                }));
    }
}
