package ru.ystu.myystu.Network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import java.io.File;

import io.reactivex.Completable;

public class LoadScheduleFromURL {

    public Completable getCompletableSchedule (String link, File file, Context mContext) {

        return Completable.create(emitter -> {

            BroadcastReceiver onComplete = null;

            try{

                final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(link));
                mRequest
                        .setTitle(file.getName())
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                        .setDestinationUri(Uri.fromFile(file))
                        .allowScanningByMediaScanner();

                final DownloadManager mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
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

                mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } catch (Exception e){
                if(!emitter.isDisposed())
                    emitter.onError(e);

                if(onComplete != null)
                    mContext.unregisterReceiver(onComplete);
            }
        });
    }
}
