package ru.ystu.myystu.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Network.UpdateService;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.NetworkInformation;

public class UpdateCheck extends Service {

    // TODO интервал обновления
    private final int DELAY_UPDATE_SEC = 10;    // Интервал проверки обновлений в сек
    private final int DELAY_WAIT_CONNECT = 5;   // Интервал проверки подключения к интернету в сек

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
                .repeatWhen(objectObservable -> {
                    if(NetworkInformation.hasConnection(mContext)) {
                        return objectObservable.delay(DELAY_UPDATE_SEC, TimeUnit.SECONDS);
                    } else {
                        return objectObservable.delay(DELAY_WAIT_CONNECT, TimeUnit.SECONDS);
                    }
                })
                .retryWhen(throwableObservable -> {
                    if(NetworkInformation.hasConnection(mContext)){
                        return throwableObservable.delay(DELAY_UPDATE_SEC, TimeUnit.SECONDS);
                    } else {
                        return throwableObservable.delay(DELAY_WAIT_CONNECT, TimeUnit.SECONDS);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {

                        if(s.equals("end")){
                            onComplete();
                        } else if(!links.contains(s)){
                            links.add(s);
                            isUpdate[0] = true;
                            showNotification(s);
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

    private void showNotification(String temp) {

        final String[] prefix = new String[]{"АСФ", "ИЭФ", "АФ", "МСФ", "ХТФ", "ЗФ", "ОУОП ЗФ"};

        final String[] var = new String[3];
        for(int i = 0; i < var.length; i++){
            var[i] = temp.substring(0, temp.indexOf("*"));
            temp = temp.substring(temp.indexOf("*") + 1);
        }

        final int idType = Integer.parseInt(var[0]);
        final int idSubType = Integer.parseInt(var[1]);
        final String link = var[2];

        String text = null;
        if(!temp.equals("")){
            text = temp.substring(temp.indexOf(":") + 2);
        }

        String title = null;
        // Обновлено расписание
        if(idType == 0){
            title = getResources().getString(R.string.bell_item_title_schedule) + " " + prefix[idSubType];
        }

        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(Color.parseColor("#ec7200"), 1, 0)
                .setContentIntent(mPendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(idType, mNotification.build());
    }
}
