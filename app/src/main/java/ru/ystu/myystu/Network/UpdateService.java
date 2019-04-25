package ru.ystu.myystu.Network;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    final private String[] prefix_f = new String[]{"Архитектурно", "Инженерно", "Автомеханический",
            "Машиностроительный", "Химико", "Заочный факультет", "Отделение ускоренных"};

    public UpdateService(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<String> checkSchedule () {

        final String url = "http://www.ystu.ru/information/students/raspisanie-zanyatiy/";
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
                                Elements els_title = null;
                                if (doc != null) {
                                    els_title = doc.getElementsByClass("single-page-description").select("h3");
                                }

                                if (els_title != null && els_title.size() > 0) {

                                    List<String> change = new ArrayList<>();

                                    for (int i = 0; i < els_title.size(); i++) {
                                        for (int id = 0 ; id < prefix_f.length; id++) {

                                            if (els_title.get(i).text().contains(prefix_f[id])) {

                                                if(els_title.get(i).text().contains(prefix_f[6])) {
                                                    id = 6;
                                                }

                                                int index = els_title.get(i).elementSiblingIndex();
                                                final Elements el = doc.getElementsByClass("single-page-description").get(0).children();

                                                Element temp;
                                                while (true) {
                                                    temp = el.get(index);
                                                    if(temp.tagName().equals("div")){
                                                        break;
                                                    }
                                                    index++;
                                                }

                                                boolean isNextChange = false;
                                                for (Element e : temp.children()) {
                                                    if (e.tagName().equals("span") && e.select("span").attr("href").isEmpty()) {

                                                        if (isNextChange) {

                                                            if(e.select("span").text().contains(":")) {
                                                                change.add(e.select("span").text());
                                                            }

                                                        } else if (e.select("span").text().equals("Изменения:")) {
                                                            isNextChange = true;
                                                        }
                                                    }
                                                }


                                                String lastChange = change.get(change.size() - 1);

                                                lastChange = lastChange
                                                        .replaceAll("&nbsp;", " ")
                                                        .replaceAll("&bsp;", " ")
                                                        .replaceAll("&sp;", " ")
                                                        .replaceAll("&p;", " ")
                                                        .replaceAll("&;", " ");

                                                if(!emitter.isDisposed() && isNew(0, id, lastChange)) {
                                                    Date mDate = new Date();
                                                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
                                                    emitter.onNext(0 + "" + id + mSimpleDateFormat.format(mDate) + "*" + lastChange);

                                                    final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("UPDATE_LIST", Context.MODE_PRIVATE);
                                                    final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                                                    mEditor.putString(0 + "" + id, mSimpleDateFormat.format(mDate) + "*" + lastChange);
                                                    mEditor.apply();
                                                }

                                                change.clear();
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

    private boolean isNew (int type, int id, String lastChange){

        if (lastChange.length() < 5) {
            return false;
        } else {
            final SharedPreferences mSharedPreferences = mContext.getSharedPreferences("UPDATE", Context.MODE_PRIVATE);
            final SharedPreferences.Editor mEditor = mSharedPreferences.edit();

            if(id < 7){
                if(mSharedPreferences.contains(type + "" + id)){
                    final String oldChange = mSharedPreferences.getString(type + "" + id, null);

                    if(!lastChange.equals(oldChange)) {
                        mEditor.putString(type + "" + id, lastChange);
                        mEditor.apply();
                        return true;
                    } else return false;
                } else {
                    mEditor.putString(type + "" + id, lastChange);
                    mEditor.apply();
                    return false;
                }
            } else return false;
        }
    }
}
