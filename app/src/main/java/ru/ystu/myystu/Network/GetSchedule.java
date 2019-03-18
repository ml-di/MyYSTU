package ru.ystu.myystu.Network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.ystu.myystu.AdaptersData.ScheduleChangeData;

public class GetSchedule {

    final String url = "https://www.ystu.ru/learning/schedule/";
    // TODO фейк ссылка с расписанием
    //final String url = "https://myystu.000webhostapp.com/myystu/schedule.txt";

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
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            if(!emitter.isDisposed())
                                emitter.onError(e);

                            client.dispatcher().executorService().shutdown();
                            client.connectionPool().evictAll();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {

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

                                if(!emitter.isDisposed()){
                                    if (link != null && link.contains("file"))
                                        emitter.onSuccess(link);
                                    else {
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                    }
                                }

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
                                    if(!emitter.isDisposed())
                                        emitter.onComplete();
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    if(!emitter.isDisposed())
                                        emitter.onError(new IllegalArgumentException("Download error"));
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

    public Single<ArrayList<ScheduleChangeData>> getChange (int id, ArrayList<ScheduleChangeData> mList){

        return Single.create(emitter -> {

            final String[] prefix_f = new String[]{"Архитектурно", "Инженерно", "Автомеханический",
                    "Машиностроительный", "Химико", "Заочный факультет", "отделение ускоренных"};

            final OkHttpClient client = new OkHttpClient();
            final Request mRequest = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            if(!emitter.isDisposed())
                                emitter.onError(e);

                            client.dispatcher().executorService().shutdown();
                            client.connectionPool().evictAll();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {

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
                                Element els = null;
                                if (doc != null) {
                                    els = doc.getElementsByClass("PaddingBorder").get(1);
                                }

                                if(mList.size() > 0)
                                    mList.clear();

                                String date;
                                String text;

                                // Поиск позиции нужной кафедры с расписанием
                                int index = -1;
                                if (els != null){
                                    for (int i = 0; i < els.children().size(); i++) {

                                        if(id == 5){
                                            if(els.children().get(i).text().equals(prefix_f[id])){
                                                index = i + 1;
                                                break;
                                            }
                                        } else {
                                            if(els.children().get(i).text().contains(prefix_f[id])){
                                                index = i + 1;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if(!emitter.isDisposed())
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                }

                                if(id == 5 || id == 6){
                                    index++;
                                }

                                // Изменения в расписание
                                Elements changes = null;
                                if(index > -1){
                                    for(int i = 0; i < els.getElementsByIndexEquals(index).size(); i++){
                                        Element mElement = els.getElementsByIndexEquals(index).get(i);
                                        if(mElement.parent().hasClass("PaddingBorder"))
                                            changes = mElement.select("li");
                                    }
                                }

                                int id = 0;
                                if (changes != null) {
                                    for(Element element : changes){
                                        date = element.select("strong").text();
                                        text = element.select("li").text();
                                        if(date.length() > 1 && text.length() > 1){
                                            text = text.substring(date.length() + 1);

                                            if(date.endsWith(":"))
                                                date = date.substring(0, date.length() - 1);

                                            mList.add(new ScheduleChangeData(id, date, text));
                                            id++;
                                        } else {
                                            if(!emitter.isDisposed())
                                                emitter.onError(new IllegalArgumentException("Not found"));
                                        }

                                    }
                                }

                                if(mList.size() > 0){
                                    if(!emitter.isDisposed()){
                                        Collections.reverse(mList);
                                        emitter.onSuccess(mList);
                                    }

                                } else {
                                    if(!emitter.isDisposed())
                                        emitter.onError(new IllegalArgumentException("Not found"));
                                }

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
}
