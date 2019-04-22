package ru.ystu.myystu.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.ystu.myystu.Activitys.MainActivity;
import ru.ystu.myystu.Network.UpdateService;
import ru.ystu.myystu.R;
import ru.ystu.myystu.Utils.NetworkInformation;

public class UpdateCheck extends Service {

    // TODO интервал обновления
    private final int DELAY_UPDATE_SEC = 15;     // Интервал проверки обновлений в сек
    private final int DELAY_WAIT_CONNECT = 5;     // Интервал проверки подключения к интернету в сек

    ArrayList<String> updates;

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

        updates = new ArrayList<>();

        update();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("tag", "start");

        if(intent != null && intent.getParcelableExtra("pending") != null){
            mPendingIntent = intent.getParcelableExtra("pending");
        }

        // TODO временное решение, пока без кеша
        if(updates.size() > 0){
            updates.clear();
        }

        Log.e("tag", mPendingIntent.getCreatorPackage());
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mDisposables.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void update(){

        // TODO Update
        final Observable<String> mObservable = mUpdateService.checkSchedule();
        final boolean[] isUpdate = {false};
        final int[] notificationCount = {0};

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
                        Log.e("Tag", s);
                        if(s.equals("end")){
                            onComplete();
                        } else if(s.startsWith("old")) {
                            final String temp = s.substring(s.indexOf("|") + 1);
                            updates.remove(temp);
                        } else if(!updates.contains(s)) {
                            updates.add(s);
                            isUpdate[0] = true;
                            notificationCount[0]++;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if(updates.size() > 0 && isUpdate[0]){

                            final Intent intent = new Intent();
                            intent.putStringArrayListExtra("schedule_update", updates);
                            if(mPendingIntent != null){
                                try {
                                    mPendingIntent.send(UpdateCheck.this, 0, intent);
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }

                            final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("NOTIFICATION_UPDATE_ID", Context.MODE_PRIVATE);
                            final SharedPreferences.Editor mEditor = mSharedPreferences.edit();

                            if(mSharedPreferences.contains("id")){
                                final String oldTemp = mSharedPreferences.getString("id", null);
                                if(!Objects.equals(oldTemp, updates.get(updates.size() - 1))){
                                    showNotification(updates.get(updates.size() - 1), notificationCount[0]);
                                    mEditor.putString("id", updates.get(updates.size() - 1));
                                }
                            } else {
                                mEditor.putString("id", updates.get(updates.size() - 1));
                                showNotification(updates.get(updates.size() - 1), notificationCount[0]);
                            }
                            mEditor.apply();

                            if(notificationCount[0] > 0){
                                notificationCount[0] = 0;
                            }

                            isUpdate[0] = false;
                        }
                    }
                }));
    }

    private void showNotification(String temp, int count) {

        final String[] prefix = new String[]{"АСФ", "ИЭФ", "АФ", "МСФ", "ХТФ", "ЗФ", "ОУОП ЗФ"};

        final int idType = Integer.parseInt(temp.substring(0, temp.indexOf("*")));
        final int idSubType = Integer.parseInt(temp.substring(temp.indexOf("*") + 1, temp.indexOf("`")));
        final String date = temp.substring(temp.indexOf("`") + 1);

        String text = null;
        String title = null;
        // Обновлено расписание
        if(idType == 0){
            title = getResources().getString(R.string.bell_item_title_schedule) + " " + prefix[idSubType];
        }

        final Intent mIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder mTaskStackBuilder = TaskStackBuilder.create(this);
        mTaskStackBuilder.addParentStack(MainActivity.class);
        mTaskStackBuilder.addNextIntent(mIntent);
        PendingIntent mPendingIntent = mTaskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(Color.parseColor("#ec7200"), 1, 0)
                .setColor(Color.argb(100, 255, 110, 0))
                .setContentIntent(mPendingIntent)
                .setNumber(count)
                .setAutoCancel(true);

        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(idType, mNotification.build());
    }
}
