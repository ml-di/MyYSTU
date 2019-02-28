package ru.ystu.myystu.Network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;

public class LoadImageFromURL {

    public Observable<Boolean> getObservableImage (String url, Context context) {

        final Observable<Boolean> observableImage = Observable.create(emitter -> {

            BroadcastReceiver onComplete = null;

            try{
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                final String date = sdf.format(new Date());

                final String fileExtenstion = url.substring(url.lastIndexOf("."));
                final String fileName = "YSTU_" + date;

                final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request
                        .setTitle(fileName)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, fileName + fileExtenstion)
                        .allowScanningByMediaScanner();

                final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null)
                    manager.enqueue(request);

                // Завершение загрузки
                onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        emitter.onNext(true);
                        emitter.onComplete();

                        context.unregisterReceiver(this);
                    }
                };

                context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } catch (Exception e){
                emitter.onError(e);
                emitter.onNext(false);

                if(onComplete != null)
                    context.unregisterReceiver(onComplete);
            }
        });

        return observableImage;
    }
}
