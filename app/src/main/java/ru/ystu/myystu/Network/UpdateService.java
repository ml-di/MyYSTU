package ru.ystu.myystu.Network;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateService {

    final private Context mContext;
    final private String[] prefix = new String[]{"asf", "ief", "af", "mf", "htf", "zf", "ozf"};
    final private String[] prefix_f = new String[]{"Архитектурно", "Инженерно", "Автомеханический",
            "Машиностроительный", "Химико", "Заочный факультет", "Отделение ускоренных"};

    public UpdateService(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<String> checkSchedule () {

        //final String url = "http://www.ystu.ru/information/students/raspisanie-zanyatiy/";
        // TODO temp url
        final String url = "https://justpaste.it/1r9t1";

        return Observable.create(emitter -> {
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
                                Elements els_title = null;
                                Elements els_desc = null;
                                if (doc != null) {
                                    els_title = doc.getElementsByClass("single-page-description").select("h3");
                                    els_desc = doc.getElementsByClass("page-main-content__spoiler js-spoiler");
                                }

                                if (els_title != null && els_title.size() > 0) {

                                    int index = 0;
                                    for (int i = 0; i < els_title.size(); i++) {
                                        for (int id = 0 ; id < prefix_f.length; id++) {

                                            if (els_title.get(i).text().contains(prefix_f[id])) {

                                                if(els_title.get(i).text().contains(prefix_f[6])) {
                                                    id = 6;
                                                }

                                                if(els_desc.get(index).parent().className().equals("single-page-description")) {
                                                    final int lastChangeSize = els_desc.get(index).text().length();
                                                    if(!emitter.isDisposed() && isNew(id, lastChangeSize)) {
                                                        Date mDate = new Date();
                                                        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
                                                        emitter.onNext("0*" + id + "`" + mSimpleDateFormat.format(mDate));
                                                    }
                                                }

                                                index++;
                                            }
                                        }
                                    }
                                }

                                if(!emitter.isDisposed()){
                                    emitter.onNext("end");
                                    emitter.onComplete();
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

    private boolean isNew (int id, int size){

        // TODO Врменное решение, потом БД
        if (size < 10) {
            return false;
        } else {
            final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("SCHEDULE_UPDATE", Context.MODE_PRIVATE);

            if(id < 7){
                if(mSharedPreferences.contains(prefix[id].toUpperCase())){
                    final int oldSize = mSharedPreferences.getInt(prefix[id].toUpperCase(), 0);
                    return size != oldSize;
                } else {
                    final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                    mEditor.putInt(prefix[id].toUpperCase(), size);
                    mEditor.apply();
                    return false;
                }
            } else return false;
        }
    }
}
