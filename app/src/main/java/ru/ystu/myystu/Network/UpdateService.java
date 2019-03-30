package ru.ystu.myystu.Network;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
            "Машиностроительный", "Химико", "Заочный факультет", "отделение ускоренных"};

    public UpdateService(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<String> checkSchedule () {

        final String url = "https://www.ystu.ru/learning/schedule/";
        // TODO temp url
        //final String url = "http://myystu.000webhostapp.com/myystu/schedule.txt";

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
                                Element els = null;
                                if (doc != null) {
                                    els = doc.getElementsByClass("PaddingBorder").get(1);
                                }

                                int index;
                                String link;

                                if (els != null) {

                                    for (int i = 0; i < els.children().size(); i++) {
                                        for(int p = 0; p < prefix_f.length; p++){

                                            if(p == 5 || p == 6)
                                                index = i + 2;
                                            else
                                                index = i + 1;

                                            if(p == 5){
                                                if(els.children().get(i).text().equals(prefix_f[p])){

                                                    link = "https://www.ystu.ru" + els.children().get(i).select("h3").select("a").attr("href");
                                                    if(isNew(p, link)){
                                                        if(!emitter.isDisposed()){
                                                                emitter.onNext("0*" + p + "*" + link + "*"
                                                                        + getChange(els, index));
                                                        }
                                                    }

                                                    break;
                                                }
                                            } else {
                                                if(els.children().get(i).text().contains(prefix_f[p])){

                                                    link = "https://www.ystu.ru" + els.children().get(i).select("h3").select("a").attr("href");
                                                    if(isNew(p, link)){
                                                        if(!emitter.isDisposed()){
                                                            emitter.onNext("0*" + p + "*" + link + "*"
                                                                    + getChange(els, index));
                                                        }
                                                    }
                                                    break;
                                                }
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

    private boolean isNew (int id, String link){

        final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("SCHEDULE_UPDATE", Context.MODE_PRIVATE);

        if(id < 7){
            if(mSharedPreferences.contains(prefix[id].toUpperCase())){
                final String oldFile = mSharedPreferences.getString(prefix[id].toUpperCase(), null);
                return !link.equals(oldFile);
            } else {
                final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putString(prefix[id].toUpperCase(), link);
                mEditor.apply();
                return false;
            }
        } else return true;
    }
    private String getChange (Element els, int index) {

        Elements temp = els.getElementsByIndexEquals(index);

        for(Element el : temp){
            if(el.parent().hasClass("PaddingBorder")){
                return el.select("li").get(el.select("li").size() - 1).text();
            }
        }

        return "";
    }

}
