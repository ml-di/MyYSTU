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

import io.reactivex.Completable;

public class LoadImageFromURL {

    public Completable getCompletableImage (String url, Context context) {

        return Completable.create(emitter -> {

            BroadcastReceiver onComplete = null;

            try{
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                final String date = sdf.format(new Date());

                final String fileExtenstion = url.substring(url.lastIndexOf("."));
                final String fileName = "YSTU_" + date;

                final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(url));
                mRequest
                        .setTitle(fileName)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, fileName + fileExtenstion)
                        .allowScanningByMediaScanner();

                final DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (mDownloadManager != null)
                    mDownloadManager.enqueue(mRequest);

                // Завершение загрузки
                onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        emitter.onComplete();
                        context.unregisterReceiver(this);
                    }
                };

                context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } catch (Exception e){
                if(!emitter.isDisposed())
                    emitter.onError(e);

                if(onComplete != null)
                    context.unregisterReceiver(onComplete);
            }
        });
    }
}
