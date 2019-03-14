package ru.ystu.myystu.Network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetSchedule {

    final String url = "https://www.ystu.ru/learning/schedule/";
    final String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};

    public boolean isNew(int id, String link, Context mContext){

        final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("SCHEDULE", Context.MODE_PRIVATE);

        if(id < 7){
            if(mSharedPreferences.contains(prefix[id].toUpperCase())){
                final String oldFile = mSharedPreferences.getString(prefix[id].toUpperCase(), null);
                return !link.equals(oldFile);
            } else
                return true;
        } else return true;
    }

    public Single<String> getLink (int id){

        return Single.create(emitter -> {

            final OkHttpClient client = new OkHttpClient();
            final String[] prefix_f = new String[]{"Архитектурно", "Инженерно", "Автомеханический",
                    "Машиностроительный", "Химико", "Заочный факультет", "отделение ускоренных"};

            final Request mRequest = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if(!emitter.isDisposed())
                                emitter.onError(e);

                            client.dispatcher().executorService().shutdown();
                            client.connectionPool().evictAll();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            try {
                                Document doc = null;
                                try {
                                    doc = Jsoup.connect(url).get();
                                } catch (IOException e) {
                                    e.printStackTrace();

                                    if(!emitter.isDisposed())
                                        emitter.onError(e);

                                    client.dispatcher().executorService().shutdown();
                                    client.connectionPool().evictAll();
                                }
                                Elements els = null;
                                if (doc != null) {
                                    els = doc.getElementsByClass("PaddingBorder").select("h3").select("a");
                                }

                                String link = null;

                                if (els != null) {
                                    for(int i = 0; i < els.size(); i++){

                                        if(id == 5){
                                            if(els.get(i).text().equals(prefix_f[id])){
                                                link = "https://www.ystu.ru" + els.get(i).attr("href");
                                                break;
                                            }
                                        } else {
                                            if(els.get(i).text().contains(prefix_f[id])){
                                                link = "https://www.ystu.ru" + els.get(i).attr("href");
                                                break;
                                            }
                                        }
                                    }
                                }

                                emitter.onSuccess(link);

                            } catch (Exception e){
                                if(!emitter.isDisposed())
                                    emitter.onError(e);
                            } finally {
                                client.dispatcher().executorService().shutdown();
                                client.connectionPool().evictAll();
                            }
                        }
                    });
        });
    }

    public Completable downloadSchedule (String link, int id, Context mContext, String dir){

        return Completable.create(emitter -> {

            BroadcastReceiver onComplete = null;

            try{

                final File patch = new File(dir + "/" + prefix[id] + ".zip");

                if(patch.exists()){
                    patch.delete();
                }

                final DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(link));
                mRequest
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                        .setDestinationUri(Uri.fromFile(patch))
                        .allowScanningByMediaScanner();

                final DownloadManager mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                if (mDownloadManager != null)
                    mDownloadManager.enqueue(mRequest);

                // Завершение загрузки
                onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        final DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_SUCCESSFUL);

                        Cursor c = null;
                        if (mDownloadManager != null) {
                            c = mDownloadManager.query(query);
                        }
                        if (c != null && c.moveToFirst()) {

                            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            switch (status) {
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    emitter.onComplete();
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    emitter.onError(null);
                                    break;
                            }

                            context.unregisterReceiver(this);
                        }
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
